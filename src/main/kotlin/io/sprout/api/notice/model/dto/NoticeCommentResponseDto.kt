package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeCommentEntity
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 공지 댓글 응답 DTO
 */
data class NoticeCommentResponseDto(
    val commentId: Long,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    val userId: Long,
    val userName: String?,
    val userProfileUrl: String?,
    val roleType: RoleType
){
    constructor(noticeComment: NoticeCommentEntity): this(
        commentId = noticeComment.id,
        content = noticeComment.content,
        createdAt = noticeComment.createdAt,
        updatedAt = noticeComment.updatedAt,

        userId = noticeComment.user.id,
        userName = noticeComment.user.name,
        userProfileUrl = noticeComment.user.profileImageUrl,
        roleType = noticeComment.user.role
    )
}