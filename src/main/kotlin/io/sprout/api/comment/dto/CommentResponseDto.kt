package io.sprout.api.comment.dto

import java.time.LocalDateTime

data class CommentResponseDto(
     val id: Long,
     val content: String,
     val userNickname: String,
     val createAt: LocalDateTime,
     val imgUrl: String,
     val postId: Long
)