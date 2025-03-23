package io.sprout.api.comment.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class commentUserDto (
     val nickname: String,
     val profileImg: String,
     val role: String
)

data class CommentResponseDto(
     val id: Long,
     val content: String,
     val userInfo: commentUserDto,
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
     val createAt: LocalDateTime,
     val imgUrl: String,
     val postId: Long
)