package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class PostCommentDto(
        val commentId: Long,
        val userNickname: String,
        val postId: Long,
        val content: String,
        val createdAt: LocalDateTime,
        val postType: String
)
