package io.sprout.api.notification.repository

import io.sprout.api.notification.entity.NotificationLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationLogRepository : JpaRepository<NotificationLogEntity, Long> {
    fun findAllByUserIdAndIsRead(userId: Long, isRead: Boolean): List<NotificationLogEntity>
    fun findAllByUserId(userId: Long): List<NotificationLogEntity>

}
