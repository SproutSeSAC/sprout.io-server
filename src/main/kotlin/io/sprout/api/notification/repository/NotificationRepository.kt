package io.sprout.api.notification.repository

import io.sprout.api.notification.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserIdAndIsRead(userId: Long, isRead: Boolean): List<NotificationEntity>
    fun findAllByUserId(userId: Long): List<NotificationEntity>

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.userId = :userId AND n.id NOT IN :idsToKeep")
    fun deleteByUserIdAndIdNotIn(userId: Long, idsToKeep: List<Long>)
}
