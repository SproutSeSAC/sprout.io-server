package io.sprout.api.notice.controller

import io.sprout.api.notice.service.NoticeEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter


@RestController
@RequestMapping("/notifications")
class NoticeSseController(
    private val noticeEventService: NoticeEventService,
) {

    @GetMapping("/admin")
    fun subscribeAdminNotifications(): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        noticeEventService.addAdminEmitter(emitter)
        return emitter
    }

    @GetMapping("/user/{userId}")
    fun subscribeUserNotifications(@PathVariable userId: Long): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        noticeEventService.addUserEmitter(userId, emitter)
        return emitter
    }
}