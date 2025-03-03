package io.sprout.api.sse.service

import io.sprout.api.notice.repository.NoticeSessionRepository
import io.sprout.api.notification.entity.NotificationDto
import io.sprout.api.notification.service.NotificationService
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService (
    private val notificationService: NotificationService,
    private val noticeSessionRepository: NoticeSessionRepository
) {
    // clientID와 SseEmitter를 저장하는 맵
    private val subscribers = ConcurrentHashMap<Long, SseEmitter>()

    // 구독 추가
    fun subscribe(clientID: Long): SseEmitter {
        val emitter = SseEmitter(0L)
        subscribers[clientID] = emitter

        // 클라이언트 종료, 타임아웃, 에러 발생 시 구독 해제
        emitter.onCompletion {
            println("클라이언트 완료: $clientID")
            subscribers.remove(clientID)
        }
        emitter.onTimeout {
            println("타임아웃: $clientID")
            subscribers.remove(clientID)
        }
        emitter.onError { e ->
            println("에러 발생 (clientID: $clientID): ${e.message}")
            subscribers.remove(clientID)
        }

        try {
            emitter.send("data: welcome\n\n", MediaType.TEXT_EVENT_STREAM)
        } catch (e: Exception) {
            println("welcome 메시지 전송 실패: ${e.message}")
        }
        return emitter
    }

    // 구독 해제
    fun unsubscribe(clientID: Long) {
        subscribers.remove(clientID)
    }

    // 메시지 발행 메서드
    fun publish(dto: NotificationDto) {
        notificationService.saveNotification(dto)
        subscribers[dto.userId]?.let { emitter ->
            try {
                emitter.send(dto.content, MediaType.TEXT_EVENT_STREAM)
            } catch (e: Exception) {
                println("메시지 전송 실패 (clientID: ${dto.userId}): ${e.message}")
                subscribers.remove(dto.userId)
            }
        }
    }

    // 헬스체크 데이터 전송 (30초 간격)
    @Scheduled(fixedRate = 30000)
    fun sendKeepAliveMessages() {
        subscribers.forEach { (clientId, emitter) ->
            try {
                emitter.send("data: check\n\n", MediaType.TEXT_EVENT_STREAM)
                println("✅ [SSE] KeepAlive 메시지 전송 성공 (clientID: $clientId)")
            } catch (e: Exception) {
                println("⚠️ [SSE ERROR] KeepAlive 메시지 전송 실패 (clientID: $clientId, 에러: ${e.message})")
                subscribers.remove(clientId)
            }
        }
    }

    // 세션 알림 전송
    @Scheduled(cron = "0 0,30 * * * *")
    fun sendSessionNotifications() {
        val now = LocalDateTime.now()

        val future30 = now.plusMinutes(31)
        val upcomingSessions = noticeSessionRepository.findSessionsAfter(now, future30)
        upcomingSessions.forEach { session ->
            session.noticeParticipants.forEach { participant ->
                val dtodata = NotificationDto(
                    fromId = session.notice.user.id,
                    userId = participant.user.id,
                    type = 8,
                    url = "",
                    content = session.notice.title,
                    NotiType = 3,
                    comment = ""
                )
                publish(dtodata)
            }
        }

        val past30 = now.minusMinutes(31)
        val pastSessions = noticeSessionRepository.findSessionsBefore(past30)
        pastSessions.forEach { session ->
            session.noticeParticipants.forEach { participant ->
                val dtodata = NotificationDto(
                    fromId = session.notice.user.id,
                    userId = participant.user.id,
                    type = 9,
                    url = "",
                    content = session.notice.title,
                    NotiType = 3,
                    comment = ""
                )
                publish(dtodata)
            }
        }
    }
}
