package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.*
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime

data class NoticeSessionResponseDto(
    val noticeSession: List<NoticeSessionCard>,

    val isLastPage: Boolean
){
    data class NoticeSessionCard(
        var postId: Long,
        val noticeId: Long,
        val title: String,
        val noticeType: NoticeType,
        val status: NoticeStatus,
        val createdAt: LocalDateTime,

        var applicationStartDateTime: LocalDateTime? = null,
        var applicationEndDateTime: LocalDateTime? = null,
        var meetingPlace: String? = null,
        var meetingType: NoticeMeetingType?,
        var participantCapacity: Int? = null,

        var writer: Writer,
        // 교육과정 대상
        var targetCourses: List<TargetCourse> = mutableListOf(),
        // 특강 세션
        var session: Session,
    )

    data class Writer(
        val userId: Long,
        val userName: String,
        val profileUrl: String,
        val role: RoleType
    )

    data class TargetCourse(
        val courseId: Long,
        val courseName: String
    )

    data class Session(
        val sessionId: Long,
        val sessionStartDateTime: LocalDateTime,
        val sessionEndDateTime: LocalDateTime,
        val ordinal: Int,
    )
}