package io.sprout.api.notification.entity

import jakarta.persistence.*

@Entity
@Table(name = "notification_log")
class NotificationLogEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "user_id", nullable = false)
        val userId: Long,

        @Column(name = "from_id", nullable = false)
        val fromId: Long,

        @Column(name = "content_type", nullable = false)
        var type: Long,

        @Column(name = "content", nullable = false)
        var content: String,

        @Column(name = "is_read", nullable = false)
        var isRead: Boolean = false
)
