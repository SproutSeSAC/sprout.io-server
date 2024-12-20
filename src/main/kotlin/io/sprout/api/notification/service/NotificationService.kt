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
}
