package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeCommentEntity
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.user.model.entities.UserEntity

/**
 * 공지사항 댓글 작성 요청 DTO
 */
data class NoticeCommentRequestDto(
    val content: String
) {
    fun toEntity(userId: Long, noticeId: Long): NoticeCommentEntity {
        return NoticeCommentEntity(
            content = this.content,
            user = UserEntity(userId),
            notice = NoticeEntity(noticeId)
        )
    }
}