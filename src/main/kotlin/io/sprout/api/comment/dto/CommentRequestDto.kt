package io.sprout.api.comment.dto

data class CommentRequestDto(
    val content: String,
    val postId: Long,
    val imgUrl: String
)
