package io.sprout.api.mypage.dto

data class PostCommentDto(
        val commentId: Long,
        val userNickname: String,
        val postId: Long,
        val content: String
)
