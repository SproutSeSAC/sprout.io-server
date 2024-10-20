package io.sprout.api.infra.sse

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalTime

//
//@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
@RequestMapping("/sse")
class SseController(
    private val sseService: SseService
) {

    @GetMapping("/initConnection/{userId}")
    fun initConnection(@PathVariable("userId") userId: Long) {
        sseService.initConnection(userId)
    }

    @GetMapping("/{userId}")
    fun userSubscribe(@PathVariable("userId") userId: Long): Flux<SseDto.Response> {
        return sseService.userSubscribe(userId)
    }

    @GetMapping("/alertCheck/{userId}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun alertCheck(@PathVariable("userId") userId: Long) {
        sseService.alertCheck(userId)
    }

    @GetMapping("/test-sse", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun testSse(): Flux<String>? {
        return Flux.interval(Duration.ofSeconds(10))
            .map { "data from server - " + LocalTime.now().toString() }
    }
}