package io.sprout.api.sse.service

import io.sprout.api.infra.sse.model.SubscriberDto
import io.sprout.api.notification.service.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService (
        private val notificationService: NotificationService
) {
    private val subscribers = ConcurrentHashMap<String, ConcurrentHashMap<Long, SubscriberDto>>()

    fun subscribe(topic: String, userId: Long): Sinks.Many<String> {
        val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
        subscribers.computeIfAbsent(topic) { ConcurrentHashMap() }[userId] = SubscriberDto(sink, true)
        return sink
    }

    fun unsubscribe(topic: String, userId: Long) {
        subscribers[topic]?.let { topicSubscribers ->
            topicSubscribers.remove(userId)
            if (topicSubscribers.isEmpty()) {
                subscribers.remove(topic)
            }
        }
    }

    fun publish(topic: String, message: String) {
        // `topic`을 userId로 간주합니다.
        val userId = topic.toLongOrNull()
                ?: throw IllegalArgumentException("토픽 미입력")

        notificationService.saveNotification(
                userId = userId,
                content = message,
        )

        subscribers[topic]?.values?.forEach { subscriber ->
            subscriber.sink.tryEmitNext(message).orThrow()
        }
    }

    fun healthCheck(topic: String, userId: Long) {
        subscribers[topic]?.get(userId)?.let {
            it.isAlive = true
        }
    }

    @Scheduled(fixedRate = 10000)
    fun removeExpiredSubscribers() {
        subscribers.entries.removeIf { (topic, topicSubscribers) ->
            topicSubscribers.entries.removeIf { (userId, subscriber) ->
                if (!subscriber.isAlive) {
                    println("만료 : $topic - $userId")
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
