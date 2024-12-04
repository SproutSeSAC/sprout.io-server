package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime


/**
 * 공지사항 검색 응답 DTO
 */
data class NoticeSearchDto(
    val noticeId: Long,

    val userId: Long,
    val username: String,
    val roleType: RoleType,

    val title: String,
    val content: String,
    val isContentOverMaxLength: Boolean,
    val viewCount: Int,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime,

    var isScraped: Boolean,
    val targetCourse: List<String>
)

data class NoticeSearchResponseDto(
    val notices: List<NoticeSearchDto>
)