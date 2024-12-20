package io.sprout.api.notification.entity

import jakarta.persistence.*
import org.springframework.beans.factory.annotation.Configurable

@Entity
@Table(name = "notification")
@Configurable
class NotificationEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "user_id", nullable = false)
        val userId: Long,

        @Column(name = "content", nullable = true)
        var content: String = "",

        @Column(name = "type", nullable = true)
        var convention: String = ""
)