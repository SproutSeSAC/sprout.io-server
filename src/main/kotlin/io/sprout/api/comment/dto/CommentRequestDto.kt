package io.sprout.api.comment.dto

data class CommentRequestDto(
    val postId: Long,
    val content: String
)