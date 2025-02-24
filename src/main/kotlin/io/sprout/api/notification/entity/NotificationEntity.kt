package io.sprout.api.notification.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
class NotificationEntity(
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
        var isRead: Boolean = false,

        @Column(name = "noti_type", nullable = false)
        var NotiType: Long,

        @Column(name = "url", nullable = false)
        var url: String,

        @Column(name = "comment", nullable = false)
        var comment : String,

        @Column(name = "createdAt", nullable = false)
        var createdAt : LocalDateTime
)
