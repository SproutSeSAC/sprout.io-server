package io.sprout.api.infra.sse

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

    fun alertCheck(userId: Long) {
        val messageList = listOf("SSE", "TEST", "ALIVE")

        sseChannel.sendToUser(
            userId = userId,
            message = SseDto.Response(
                text = messageList.toString()
            )
        )
    }

//    fun sendAlert(request: MessageDto.AlertRequest) {
//        val messageList = listOf("SSE", "TEST", "ALIVE")
//
//        sseChannel.sendToUsers(
//            userIds = request.uid,
//            message = SseDto.Response(
//                text = messageList.toString()
//            )
//        )
//    }

    // sse는 infra로 빼고, 메세지 저장하는 service 생성


}