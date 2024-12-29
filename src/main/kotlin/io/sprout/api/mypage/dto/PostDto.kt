package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class PostDto(
        val postId: Long,
        val clientId: Long,
        val postType: String,
        val title: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
