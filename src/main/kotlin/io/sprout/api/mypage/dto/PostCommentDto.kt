package io.sprout.api.mypage.dto

data class PostCommentDto(
        val commentId: Long,
        val userId: Long,
        val postId: Long,
        val content: String
)
