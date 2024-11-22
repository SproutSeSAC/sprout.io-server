package io.sprout.api.infra.sse.controller

import io.sprout.api.infra.sse.service.SseService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/sse")
class SseController(private val sseService: SseService) {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String?>? {
        println("Exception 발생 : ${e.message}")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    @GetMapping("/subscribe/{topic}/{clientID}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "SSE 구독 시작", description = "Topic은 Role, ClientID는 말 그대로 ID.")
    fun subscribe(@PathVariable topic: String, @PathVariable clientID: String): Flux<String> {
        val sink = sseService.subscribe(topic, clientID)
        return sink.asFlux()
                .doFinally {
                    println("클라이언트 종료 : $topic - $clientID")
                    sseService.unsubscribe(topic, clientID)
                }
    }

    @DeleteMapping("/unsubscribe/{topic}/{clientID}")
    @Operation(summary = "SSE 구독 종료", description = "Topic은 Role, ClientID는 말 그대로 ID.")
    fun unsubscribe(@PathVariable topic: String, @PathVariable clientID: String) {
        sseService.unsubscribe(topic, clientID)
    }

    @PostMapping("/publish/{topic}")
    @Operation(summary = "SSE 데이터 발행", description = "특정 Topic으로 데이터 전송")
    fun publish(@PathVariable topic: String, @RequestBody message: String) {
        sseService.publish(topic, message)
    }

    @PostMapping("/health/{topic}/{clientID}")
    @Operation(summary = "SSE 연결 확인", description = "10초 주기로 검사하기 때문에, react에선 좀 빠르게 해 주어야 함.")
    fun healthCheck(@PathVariable topic: String, @PathVariable clientID: String) {
        sseService.healthCheck(topic, clientID)
    }
}
