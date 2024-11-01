package io.sprout.api.notice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.sprout.api.notice.model.dto.NoticeNotification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class NoticeEventService(private val objectMapper: ObjectMapper) {
    private val logger = LoggerFactory.getLogger(NoticeEventService::class.java)
    private val adminEmitters = ConcurrentHashMap<Long, SseEmitter>()
    private val userEmitters = ConcurrentHashMap<Long, SseEmitter>()

    // 모든 SseEmitter가 공유하는 단일 스레드 풀 생성 (최대 10개 스레드 사용)
    private val scheduledExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(10)

    fun addAdminEmitter(emitter: SseEmitter) {
        val adminId = 1L
        adminEmitters[adminId] = emitter
        handleEmitter(emitter, adminId, adminEmitters, "관리자", scheduledExecutor)
    }

    fun addUserEmitter(userId: Long, emitter: SseEmitter) {
        userEmitters[userId] = emitter
        handleEmitter(emitter, userId, userEmitters, "사용자", scheduledExecutor)
    }

    private fun <K> handleEmitter(
        emitter: SseEmitter,
        id: K,
        emittersMap: ConcurrentHashMap<K, SseEmitter>,
        userType: String,
        scheduledExecutor: ScheduledExecutorService
    ) {
        // 60초마다 Ping 이벤트 전송
        val scheduledTask = scheduledExecutor.scheduleAtFixedRate({
            try {
                emitter.send(SseEmitter.event().name("ping").data("keep-alive"))
            } catch (e: IOException) {
                // 연결이 닫힌 상태에서 전송을 시도하면 발생하는 예외를 무시하고 로그를 DEBUG로 남깁니다.
                logger.debug("SSE 연결이 닫힌 상태에서 데이터 전송 시도: $userType ID $id", e)
                emittersMap.remove(id)
                logger.info("SSE 연결이 종료되었습니다. $userType ID: $id")
            }
        }, 0, 60, TimeUnit.SECONDS) // 60초마다 Ping 이벤트 전송

        emitter.onCompletion {
            scheduledTask.cancel(true)
            emittersMap.remove(id)
            logger.info("SSE 연결이 정상적으로 종료되었습니다. $userType ID: $id")
        }

        emitter.onTimeout {
            scheduledTask.cancel(true)
            emittersMap.remove(id)
            logger.warn("SSE 연결이 타임아웃으로 종료되었습니다. $userType ID: $id")
        }

        emitter.onError {
            scheduledTask.cancel(true)
            emittersMap.remove(id)
            logger.error("SSE 연결에서 오류가 발생했습니다. $userType ID: $id")
        }
    }

    fun sendToAdmin(notification: NoticeNotification) {
        val jsonMessage = objectMapper.writeValueAsString(notification)
        adminEmitters.values.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event().name("notice").data(jsonMessage))
            } catch (e: IOException) {
                logger.debug("관리자에게 메시지 전송 실패", e)
            }
        }
    }

    fun sendToUser(userId: Long, notification: NoticeNotification) {
        val jsonMessage = objectMapper.writeValueAsString(notification)
        userEmitters[userId]?.let { emitter ->
            try {
                emitter.send(SseEmitter.event().name("notice").data(jsonMessage))
            } catch (e: IOException) {
                logger.debug("사용자 $userId 에게 메시지 전송 실패", e)
            }
        }
    }
}
