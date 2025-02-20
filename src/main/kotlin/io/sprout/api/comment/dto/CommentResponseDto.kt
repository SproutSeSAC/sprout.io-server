package io.sprout.api.comment.dto

import java.time.LocalDateTime

data class commentUserDto (
     val nickname: String,
     val profileImg: String
)

data class CommentResponseDto(
     val id: Long,
     val content: String,
     val userInfo: commentUserDto,
     val createAt: LocalDateTime,
     val imgUrl: String,
     val postId: Long
)