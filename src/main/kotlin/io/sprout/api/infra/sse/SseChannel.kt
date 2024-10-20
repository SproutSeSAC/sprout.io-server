package io.sprout.api.infra.sse

import io.sprout.api.config.exception.BaseException
import io.sprout.api.config.exception.ExceptionCode
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SseChannel {
    // 각 사용자마다 sinks를 관리 진행
    private val userEventMap: ConcurrentHashMap<Long, Sinks.Many<SseDto.Response>> = ConcurrentHashMap()
    private val allEvent: Sinks.Many<SseDto.Response> = Sinks.many().multicast().directAllOrNothing()

    fun createUserFlux(userId: Long): Flux<SseDto.Response> {
        return userEventMap.computeIfAbsent(userId) {
            Sinks.many().multicast().directAllOrNothing()
        }.asFlux()
    }

    fun initConnection(userId: Long) {
        val message = SseDto.Response(
            text = "Initiating connection"
        )
        allEvent.tryEmitNext(message)
        userEventMap[userId]?.tryEmitNext(message)
    }

    fun sendToUser(userId: Long, message: SseDto.Response) {
        try {
            userEventMap[userId]?.tryEmitNext(message)
        } catch (e: Exception) {
            throw BaseException(ExceptionCode.NOT_FOUND_CONTENTS)
        }
    }

    fun sendToUsers(userIds: MutableSet<Long>, message: SseDto.Response) {
        try {
            userIds.forEach {
                userEventMap[it]?.tryEmitNext(message)
            }
        } catch (e: Exception) {
            throw BaseException(ExceptionCode.NOT_FOUND_CONTENTS)
        }
    }
}