package io.sprout.api.message.controller

import io.sprout.api.message.model.dto.SseDto
import io.sprout.api.message.service.SseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux


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
}