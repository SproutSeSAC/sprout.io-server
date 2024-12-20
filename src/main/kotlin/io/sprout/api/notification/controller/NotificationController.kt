package io.sprout.api.notification.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.notification.entity.NotificationEntity
import io.sprout.api.notification.service.NotificationService
import io.swagger.v3.oas.annotations.Operation
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/noti")
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

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회 API", description = "읽지 않은 알림만 가져옵니다.")
    fun getUnreadNotifications(): ResponseEntity<List<NotificationEntity>> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val notifications = notificationService.getUnreadNotificationsByUserId(clientID.toLong())
        return ResponseEntity.ok(notifications)
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 상태 업데이트 API", description = "알림을 읽음 상태로 표시합니다.")
    fun markNotificationAsRead(@PathVariable notificationId: Long): ResponseEntity<Boolean> {
        val result = notificationService.markNotificationAsRead(notificationId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "알림 삭제 API",
            description = "특정 알림을 삭제합니다."
    )
    fun deleteNotification(@PathVariable notificationId: Long): ResponseEntity<Boolean> {
        return try {
            val result = notificationService.deleteNotification(notificationId)
            ResponseEntity.ok(result)
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/all")
    @Operation( summary = "알림 전체 삭제 API (일괄)", description = "해당 유저의 알림을 전부 삭제합니다.")
    fun deleteAllNotification(): ResponseEntity<Boolean> {
        return try {
            val clientID = securityManager.getAuthenticatedUserName()
                    ?: return ResponseEntity.status(401).build()

            val result = notificationService.deleteAllNotification(clientID)
            ResponseEntity.ok(result)
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/all")
    @Operation(summary = "알림 읽음 처리 API (일괄)", description = "해당 유저의 모든 알림을 읽음 상태로 표시합니다.")
    fun markAllNotificationsAsRead(): ResponseEntity<Boolean> {
        return try {
            val clientID = securityManager.getAuthenticatedUserName()
                    ?: return ResponseEntity.status(401).build()

            val result = notificationService.markAllNotificationsAsRead(clientID)
            ResponseEntity.ok(result)
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(404).body(false)
        }
    }
}
