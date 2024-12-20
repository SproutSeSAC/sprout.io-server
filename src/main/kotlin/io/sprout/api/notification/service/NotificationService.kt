package io.sprout.api.notification.service

import io.sprout.api.notification.entity.NotificationEntity
import io.sprout.api.notification.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
        private val notificationRepository: NotificationRepository
) {
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
        return notificationRepository.save(notification)
    }

    @Transactional(readOnly = true)
    fun getUnreadNotificationsByUserId(userId: Long): List<NotificationEntity> {
        return notificationRepository.findAllByUserIdAndIsRead(userId, isRead = false)
    }

    @Transactional
    fun markNotificationAsRead(notificationId: Long): Boolean {
        val notification = notificationRepository.findById(notificationId)
                .orElseThrow { IllegalArgumentException("알림 ID를 찾을 수 없음 : $notificationId") }
        notification.isRead = true
        notificationRepository.save(notification)
        return true
    }
}
