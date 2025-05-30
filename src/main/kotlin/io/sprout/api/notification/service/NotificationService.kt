package io.sprout.api.notification.service

import io.sprout.api.notification.entity.NotificationDto
import io.sprout.api.notification.entity.NotificationRequestDto
import io.sprout.api.notification.entity.NotificationEntity
import io.sprout.api.notification.entity.NotificationLogEntity
import io.sprout.api.notification.repository.NotificationLogRepository
import io.sprout.api.notification.repository.NotificationRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationService(
        private val notificationRepository: NotificationRepository,
        private val notificationLogRepository: NotificationLogRepository
) {
    private val MAX_NOTIFICATIONSCOUNT = 20 // 우선 20개

    val userLocks = ConcurrentHashMap<Long, Any>()

    fun getUserLock(userId: Long): Any =
        userLocks.computeIfAbsent(userId) { Any() }

    @Transactional(readOnly = true)
    fun getNotificationsByUserId(userId: Long): List<NotificationRequestDto> {
        val data = notificationRepository.findAllByUserId(userId)

        val x = data.map { m_data ->
            NotificationRequestDto(
                m_data.id,
                m_data.type,
                m_data.NotiType,
                m_data.content,
                m_data.url,
                m_data.comment,
                m_data.isRead,
                m_data.createdAt
            )
        }

        return x.reversed()
    }

    @Transactional
    fun saveNotification(dto: NotificationDto): NotificationEntity {
        synchronized(getUserLock(dto.userId)) {
            val createAt = LocalDateTime.now()

            val notification = NotificationEntity(
                userId = dto.userId,
                fromId = dto.fromId,
                type = dto.type,
                content = dto.content,
                url = dto.url,
                NotiType = dto.NotiType,
                comment = dto.comment,
                createdAt = createAt
            )

            val notification_log = NotificationLogEntity(
                userId = notification.userId,
                fromId = notification.fromId,
                type = notification.type,
                content = notification.content,
                url = notification.url,
                NotiType = notification.NotiType,
                comment = notification.comment,
                createdAt = notification.createdAt
            )

            val savedNotification = notificationRepository.save(notification)
            notificationLogRepository.save(notification_log)

            maxNotifications(dto.userId)

            return savedNotification
        }
    }

    @Transactional
    fun maxNotifications(userId: Long) {
        val notifications = notificationRepository.findAllByUserId(userId)
        if (notifications.size > MAX_NOTIFICATIONSCOUNT) {
            val idsToKeep = notifications.sortedByDescending { it.createdAt }
                .take(MAX_NOTIFICATIONSCOUNT)
                .map { it.id }

            notificationRepository.deleteByUserIdAndIdNotIn(userId, idsToKeep)
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

    @Transactional
    fun deleteAllNotification(clientId: Long): Boolean {
        synchronized(getUserLock(clientId)) {
            return try {
                val notifications = notificationRepository.findAllByUserId(clientId)
                notificationRepository.deleteAll(notifications)
                true
            } catch (e: Exception) {
                println("알림 삭제 오류 : ${e.message}")
                false
            }
        }
    }

    @Transactional
    fun markAllNotificationsAsRead(clientId: Long): Boolean {
        return try {
            val notifications = notificationRepository.findAllByUserId(clientId)
            notifications.forEach { notification ->
                if (!notification.isRead) {
                    notification.isRead = true
                }
            }
            notificationRepository.saveAll(notifications)
            true
        } catch (e: Exception) {
            println("모든 알림 읽음 처리 오류: ${e.message}")
            false
        }
    }
}
