package io.sprout.api.sse.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.sse.service.SseService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/sse")
class SseController(
    private val sseService: SseService,
    private val securityManager: SecurityManager
) {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String?>? {
        println("Exception 발생 : ${e.message}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    @GetMapping("/subscribe/{topic}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "SSE 구독 시작", description = "Topic은 Role.")
    fun subscribe(@PathVariable topic: String): ResponseEntity<Flux<String>> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)

        val sink = sseService.subscribe(topic, clientID)
        return ResponseEntity.ok(
                sink.asFlux()
                        .doFinally {
                            println("클라이언트 종료 : $topic - $clientID")
                            sseService.unsubscribe(topic, clientID)
                        }
        )
    }

    @DeleteMapping("/unsubscribe/{topic}")
    @Operation(summary = "SSE 구독 종료", description = "Topic은 Role.")
    fun unsubscribe(@PathVariable topic: String): ResponseEntity<String> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.")

        sseService.unsubscribe(topic, clientID)
        return ResponseEntity.ok("구독이 해제되었습니다.")
    }

    @PostMapping("/publish/{topic}")
    @Operation(summary = "SSE 데이터 발행", description = "특정 Topic으로 데이터 전송")
    fun publish(@PathVariable topic: String, @RequestBody message: String): ResponseEntity<String> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.")

        sseService.publish(topic, message)
        return ResponseEntity.ok("메시지가 발행되었습니다.")
    }

    @PostMapping("/health/{topic}")
    @Operation(summary = "SSE 연결 확인", description = "10초 주기로 검사하기 때문에, react에선 좀 빠르게 해 주어야 함.")
    fun healthCheck(@PathVariable topic: String): ResponseEntity<String> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.")

        sseService.healthCheck(topic, clientID)
        return ResponseEntity.ok("연결 상태가 확인되었습니다.")
    }
}
