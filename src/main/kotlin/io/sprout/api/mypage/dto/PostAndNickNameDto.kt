package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class PostAndNickNameDto(
        val postId: Long,
        val linkedId: Long,
        val clientId: Long,
        val postType: String,
        val title: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val createdNickName: String
)
