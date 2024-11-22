package io.sprout.api.infra.sse

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/sse")
class SseController {
    // IO 에러 대응
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String?>? {
        println("Exception 발생 : ${e.message}");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    }

    // 편의성 구조체
    data class Subscriber(
            val sink: Sinks.Many<String>,
            var isAlive: Boolean
    )

    private val subscribers = ConcurrentHashMap<String, ConcurrentHashMap<String, Subscriber>>()

    @GetMapping("/subscribe/{topic}/{clientID}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "SSE 구독 시작", description = "Topic은 Role, ClientID는 말 그대로 ID.")
    fun subscribe(@PathVariable topic: String, @PathVariable clientID: String): Flux<String> {
        val sink = Sinks.many().multicast().onBackpressureBuffer<String>()
        subscribers.computeIfAbsent(topic) { ConcurrentHashMap() }[clientID] = Subscriber(sink, true)

        return sink.asFlux()
                .doFinally {
                    println("클라이언트 종료 : $topic - $clientID")
                    subscribers[topic]?.let { topicSubscribers ->
                        topicSubscribers.remove(clientID)
                        if (topicSubscribers.isEmpty()) {
                            subscribers.remove(topic)
                        }
                    }
                }
    }

    @DeleteMapping("/unsubscribe/{topic}/{clientID}")
    @Operation(summary = "SSE 구독 종료", description = "Topic은 Role, ClientID는 말 그대로 ID.")
    fun unsubscribe(@PathVariable topic: String, @PathVariable clientID: String) {
        subscribers[topic]?.let { topicSubscribers ->
            topicSubscribers.remove(clientID)
            println("구독 종료 : $topic - $clientID")
            if (topicSubscribers.isEmpty()) {
                subscribers.remove(topic)
            }
        }
    }

    @PostMapping("/publish/{topic}")
    @Operation(summary = "SSE 데이터 발행", description = "특정 Topic으로 데이터 전송")
    fun publish(@PathVariable topic: String, @RequestBody message: String) {
        subscribers[topic]?.values?.forEach { subscriber ->
            subscriber.sink.tryEmitNext(message).orThrow()
        }
    }

    // *-- 이 아래는 할지 안 할지 나중에 결정 --* //
    // 헬스 체크
    @PostMapping("/health/{topic}/{clientID}")
    @Operation(summary = "SSE 연결 확인", description = "10초 주기로 검사하기 때문에, react에선 좀 빠르게 해 주어야 함.")
    fun healthCheck(@PathVariable topic: String, @PathVariable clientID: String) {
        subscribers[topic]?.get(clientID)?.let {
            it.isAlive = true
            println("헬스 체크 : $topic - $clientID")
        }
    }

    // 헬스 체크 정리
    @Scheduled(fixedRate = 10000) // 10초
    fun removeExpiredSubscribers() {
        subscribers.entries.removeIf { (topic, topicSubscribers) ->
            topicSubscribers.entries.removeIf { (clientID, subscriber) ->
                if (!subscriber.isAlive) {
                    println("만료 : $topic - $clientID")
                    true
                } else {
                    subscriber.isAlive = false
                    false
                }
            }
            topicSubscribers.isEmpty()
        }
    }

}