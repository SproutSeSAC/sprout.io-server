package io.sprout.api.notification.repository

import io.sprout.api.notification.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserIdAndIsRead(userId: Long, isRead: Boolean): List<NotificationEntity>
    fun findAllByUserId(userId: Long): List<NotificationEntity>

}
