package io.sprout.api.notification.entity

data class NotificationDto(
        val id: Long = 0,
        val userId: Long,
        val fromId: Long,
        var type: Long,
        var content: String,
        var isRead: Boolean = false,
        var NotiType: Long,
        var url: String,
        var comment : String,
)
