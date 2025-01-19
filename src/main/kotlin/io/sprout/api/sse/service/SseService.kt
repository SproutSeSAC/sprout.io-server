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
    private val subscribers = ConcurrentHashMap<Long, SubscriberDto>()

    // 구독 추가
    fun subscribe(clientID: Long): Sinks.Many<String> {
        val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
        subscribers[clientID] = SubscriberDto(sink, true)
        return sink
    }

    // 구독 해제
    fun unsubscribe(clientID: Long) {
        subscribers.remove(clientID)
    }

    // 메시지 발행 메서드
    fun publish(publishID: Long, clientID: Long, message: String) {
        notificationService.saveNotification(clientID, message)
        subscribers[clientID]?.sink?.tryEmitNext(message)?.orThrow()
    }
}
