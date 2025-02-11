package io.sprout.api.notification.entity

data class NotificationRequestDto (
    val id: Long = 0,
    var type: Long,
    var NotiType: Long,
    var content: String,
    var url: String,
    var comment : String,
    var isRead: Boolean = false
)