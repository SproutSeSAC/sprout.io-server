package io.sprout.api.infra.sse.service

import io.sprout.api.infra.sse.model.SubscriberDto
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService {
    private val subscribers = ConcurrentHashMap<String, ConcurrentHashMap<String, SubscriberDto>>()

    fun subscribe(topic: String, clientID: String): Sinks.Many<String> {
        val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
        subscribers.computeIfAbsent(topic) { ConcurrentHashMap() }[clientID] = SubscriberDto(sink, true)
        return sink
    }

    fun unsubscribe(topic: String, clientID: String) {
        subscribers[topic]?.let { topicSubscribers ->
            topicSubscribers.remove(clientID)
            if (topicSubscribers.isEmpty()) {
                subscribers.remove(topic)
            }
        }
    }

    fun publish(topic: String, message: String) {
        subscribers[topic]?.values?.forEach { subscriber ->
            subscriber.sink.tryEmitNext(message).orThrow()
        }
    }

    fun healthCheck(topic: String, clientID: String) {
        subscribers[topic]?.get(clientID)?.let {
            it.isAlive = true
        }
    }

    @Scheduled(fixedRate = 10000)
    fun removeExpiredSubscribers() {
        subscribers.entries.removeIf { (topic, topicSubscribers) ->
            topicSubscribers.entries.removeIf { (clientID, subscriber) ->
                if (!subscriber.isAlive) {
                    println("만료 : $topic - $clientID")
                    true
                } else {
                    subscriber.isAlive = false
                    false
                }
            }
            topicSubscribers.isEmpty()
        }
    }
}