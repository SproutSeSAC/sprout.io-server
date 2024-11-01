package io.sprout.api.notice.model.dto

data class NoticeNotification(
    val noticeId: Long,
    val userId: Long,
    val userName: String,
    val userNickName: String,
    val message: String
)