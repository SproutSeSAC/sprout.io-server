package io.sprout.api.notification.service

import io.sprout.api.notification.entity.NotificationEntity
import io.sprout.api.notification.repository.NotificationRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
        private val notificationRepository: NotificationRepository
) {
    private val MAX_NOTIFICATIONSCOUNT = 20 // 우선 20개

    @Transactional(readOnly = true)
    fun getNotificationsByUserId(userId: Long): List<NotificationEntity> {
        return notificationRepository.findAllByUserId(userId)
    }

    @Transactional
    fun saveNotification(userId: Long, content: String): NotificationEntity {
        val notification = NotificationEntity(
                userId = userId,
                content = content
        )
        val savedNotification = notificationRepository.save(notification)

        maxNotifications(userId)

        return savedNotification
    }

    @Transactional
    fun maxNotifications(userId: Long) {
        val notifications = notificationRepository.findAllByUserId(userId)
        if (notifications.size > MAX_NOTIFICATIONSCOUNT) {
            val excessCount = notifications.size - MAX_NOTIFICATIONSCOUNT
            val toDelete = notifications.sortedBy { it.id }.take(excessCount)

            toDelete.forEach { notification ->
                notificationRepository.delete(notification)
            }
        }
    }

    @Transactional(readOnly = true)
    fun getUnreadNotificationsByUserId(userId: Long): List<NotificationEntity> {
        return notificationRepository.findAllByUserIdAndIsRead(userId, isRead = false)
    }

    @Transactional
    fun markNotificationAsRead(notificationId: Long): Boolean {
        val notification = notificationRepository.findById(notificationId)
                .orElseThrow { EntityNotFoundException("알림 ID를 찾을 수 없음 : $notificationId") }
        notification.isRead = true
        notificationRepository.save(notification)
        return true
    }

    @Transactional
    fun deleteNotification(notificationId: Long): Boolean {
        val notification = notificationRepository.findById(notificationId)
                .orElseThrow { EntityNotFoundException("알림 ID를 찾을 수 없음 : $notificationId") }

        notificationRepository.delete(notification)
        return true
    }
}
