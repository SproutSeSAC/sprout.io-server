package io.sprout.api.notification.repository

import io.sprout.api.notification.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserId(userId: Long): List<NotificationEntity>
}
