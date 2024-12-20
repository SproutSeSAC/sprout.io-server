package io.sprout.api.notification.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.notification.entity.NotificationEntity
import io.sprout.api.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationController(
        private val notificationService: NotificationService,
        private val securityManager: SecurityManager
) {
    @GetMapping
    @Operation(
            summary = "알림 목록 조회 API",
            description = "현재 클라이언트의 ID를 기반으로 알림 데이터를 전부 가져옵니다."
    )
    fun getNotification(): ResponseEntity<List<NotificationEntity>> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val notifications = notificationService.getNotificationsByUserId(clientID)
        return ResponseEntity.ok(notifications)
    }
}
