package io.sprout.api.notice.model.dto

import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime

data class NoticeCardDto(
    val postId: Long?,
    val noticeId: Long,
    val title: String,
    val applicationEndDate: LocalDateTime,

    val manager: Manager
) {
  data class Manager(
      val userId: Long,
      val name: String,
      val nickname: String,
      val role: RoleType
  )
}
