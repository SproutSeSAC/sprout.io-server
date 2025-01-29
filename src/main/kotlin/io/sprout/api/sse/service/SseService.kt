package io.sprout.api.sse.service

import io.sprout.api.infra.sse.model.SubscriberDto
import io.sprout.api.notice.model.entities.NoticeSessionEntity
import io.sprout.api.notice.repository.NoticeSessionRepository
import io.sprout.api.notification.service.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService (
    private val notificationService: NotificationService,
    private val noticeSessionRepository: NoticeSessionRepository
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
        notificationService.saveNotification(clientID, publishID, message)
        subscribers[clientID]?.sink?.tryEmitNext(message)?.orThrow()
    }

    @Scheduled(fixedRate = 30000) // 30초
    fun sendKeepAliveMessages() {
        subscribers.forEach { (it, subscriber) ->
            subscriber.sink.tryEmitNext("check").orThrow()
        }
    }

    @Scheduled(cron = "0 0,30 * * * *")
    fun sendSessionNotifications() {
        val now = LocalDateTime.now()

        val future30 = now.plusMinutes(31)
        val upcomingSessions = noticeSessionRepository.findSessionsAfter(now, future30)
        upcomingSessions.forEach { session ->
            session.noticeParticipants.forEach { participant ->
                publish(
                    session.notice.user.id,
                    participant.user.id,
                    "8,곧 ${session.notice.title}이 시작됩니다! 장소를 확인해주세요."
                )
            }
        }

        val past30 = now.minusMinutes(31)
        val pastSessions = noticeSessionRepository.findSessionsBefore(past30)
        pastSessions.forEach { session ->
            session.noticeParticipants.forEach { participant ->
                publish(
                    session.notice.user.id,
                    participant.user.id,
                    "9,${session.notice.title}은 어떠셨나요? 만족도 조사에 참여해 주세요!"
                )
            }
        }
    }

}
