package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.*
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime

data class NoticeDetailResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val noticeType: NoticeType,
    val viewCount: Int,
    val status: NoticeStatus,

    var applicationStartDateTime: LocalDateTime? = null,
    var applicationEndDateTime: LocalDateTime? = null,
    var meetingPlace: String? = null,
    var meetingType: NoticeMeetingType?,
    var applicationForm: String? = null,
    var satisfactionSurvey: String? = null,
    var participantCapacity: Int? = null,

    var writer: Writer,
    // 교육과정 대상
    var targetCourses: List<TargetCourse> = mutableListOf(),
    // 특강 세션
){

    var isScraped: Boolean = false
    var sessions: List<Session>? = mutableListOf()

    constructor(notice: NoticeEntity) :this(
        id = notice.id,
        title = notice.title,
        content = notice.content,
        noticeType = notice.noticeType,
        viewCount = notice.viewCount,
        status = notice.status,
        applicationStartDateTime = notice.applicationStartDateTime,
        applicationEndDateTime = notice.applicationEndDateTime,
        applicationForm = notice.applicationForm,
        meetingType = notice.meetingType,
        meetingPlace = notice.meetingPlace,
        participantCapacity = notice.participantCapacity,
        satisfactionSurvey = notice.satisfactionSurvey,

        writer = Writer(notice.user.id,
            notice.user.name ?: "임의의 사용자",
            notice.user.profileImageUrl ?: "",
            notice.user.role),
        targetCourses = notice.targetCourses.map { TargetCourse(
            it.course.id,
            it.course.title) }.toList(),
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
        val participantCount: Long,
        val currentStatus: ParticipantStatus?
    )
}