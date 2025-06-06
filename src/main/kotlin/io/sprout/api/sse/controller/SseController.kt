package io.sprout.api.sse.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.notification.entity.NotificationDto
import io.sprout.api.sse.service.SseService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class SseController(
    private val sseService: SseService,
    private val securityManager: SecurityManager
) {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String?> {
        println("Exception 발생 : ${e.message}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    // SSE 구독
    @GetMapping("/subscribe", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "SSE 구독 시작", description = "clientID(Long 타입)를 기반으로 SSE 구독")
    fun subscribe(): ResponseEntity<SseEmitter> {
        val clientID = securityManager.getAuthenticatedUserId()

        val emitter = sseService.subscribe(clientID)
        return ResponseEntity.ok(emitter)
    }

    // SSE 구독 해제
    @DeleteMapping("/unsubscribe")
    @Operation(summary = "SSE 구독 종료", description = "clientID(Long 타입)를 기반으로 구독 해제")
    fun unsubscribe(): ResponseEntity<String> {
        val clientID = securityManager.getAuthenticatedUserId()

        sseService.unsubscribe(clientID)
        return ResponseEntity.ok("구독이 해제되었습니다.")
    }

    // SSE 메시지 발행
    @PostMapping("/publish/{clientID}")
    @Operation(summary = "SSE 데이터 발행", description = "특정 clientID로 데이터 전송")
    fun publish(@PathVariable clientID: Long, @RequestBody message: String): ResponseEntity<String> {
        val publisherID = securityManager.getAuthenticatedUserId()

        val dtodata = NotificationDto(
            fromId = publisherID,
            userId = clientID,
            type = 15,
            url = "",
            content = message,
            NotiType = 6,
            comment = ""
        )

        sseService.publish(dtodata)
        return ResponseEntity.ok("메시지가 발행되었습니다.")
    }
}