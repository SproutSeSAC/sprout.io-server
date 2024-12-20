package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class PostScrapDto(
        val postScrapId: Long,
        val userId: Long,
        val postId: Long,
        val createdAt: LocalDateTime
)