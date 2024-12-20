package io.sprout.api.scrap.dto

import java.time.LocalDateTime

data class ScrapResponseDto(
        val id: Long,
        val userId: Long,
        val postId: Long,
        val createdAt: LocalDateTime
)
