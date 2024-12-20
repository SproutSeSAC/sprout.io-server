package io.sprout.api.notification.entity

import jakarta.persistence.*

@Entity
@Table(name = "notification")
class NotificationEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "user_id", nullable = false)
        val userId: Long,

        @Column(name = "content", nullable = false)
        var content: String,

        @Column(name = "is_read", nullable = false)
        var isRead: Boolean = false
)
