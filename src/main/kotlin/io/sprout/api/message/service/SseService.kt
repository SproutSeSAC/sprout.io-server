package io.sprout.api.message.service

import io.sprout.api.message.model.dto.SseDto
import io.sprout.api.message.sse.SseChannel
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SseService(
    private val sseChannel: SseChannel
) {
    fun userSubscribe(userId: Long): Flux<SseDto.Response> {
        return sseChannel.createUserFlux(userId)
    }

    fun initConnection(userId: Long) {
        sseChannel.initConnection(userId)
    }

}