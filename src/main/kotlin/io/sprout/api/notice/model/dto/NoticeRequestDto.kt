package io.sprout.api.notice.model.dto

import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.notice.model.entities.*
import io.sprout.api.user.model.entities.UserEntity
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

/**
 * 세션이 있는 공지사항 생성 요청 DTO
 * (특강, 행사 타입)
 */
data class NoticeRequestDto(
    val targetCourseIdList: Set<Long>,

    @field:NotBlank(message = "제목은 비어있으면 안됩니다.")
    val title: String,

    @field:NotBlank(message = "내용은 비어있으면 안됩니다.")
    val content: String,

    val noticeType: NoticeType,

    val isPhoneNumberRequired: Boolean,

    val applicationStartDateTime: LocalDateTime?,

    val applicationEndDateTime: LocalDateTime?,

    val participantCapacity: Int?,

    val meetingType: NoticeMeetingType?,

    val meetingPlace: String?,

    val satisfactionSurvey: String?,

    val sessions: List<NoticeSessionDTO> = mutableListOf()
) {

    fun toNormalEntity(userId: Long): NoticeEntity {
        val noticeEntity = NoticeEntity(
            title = this.title,
            content = this.content,
            user = UserEntity(userId),
            status = NoticeStatus.ACTIVE,
            noticeType = this.noticeType,
            meetingType = null,
            viewCount = 0,
            isPhoneNumberRequired = false,
            meetingPlace = null,
            applicationEndDateTime = null,
            applicationStartDateTime = null,
            participantCapacity = null,
            satisfactionSurvey = null
        )

        noticeEntity.targetCourses.plusAssign(targetCourseIdList.map { NoticeTargetCourseEntity(
            notice = noticeEntity,
            course = CourseEntity(it)
        ) }
            .toMutableSet())

        return noticeEntity
    }
    fun toSessionEntity(userId: Long): NoticeEntity {
        val noticeEntity = NoticeEntity(
            title = this.title,
            content = this.content,
            user = UserEntity(userId),
            status = NoticeStatus.ACTIVE,
            noticeType = this.noticeType,
            meetingType = this.meetingType,
            viewCount = 0,
            isPhoneNumberRequired = this.isPhoneNumberRequired,
            meetingPlace = this.meetingPlace,
            applicationEndDateTime = this.applicationEndDateTime,
            applicationStartDateTime = this.applicationStartDateTime,
            participantCapacity = this.participantCapacity,
            satisfactionSurvey = this.satisfactionSurvey
        )

        noticeEntity.targetCourses.plusAssign(targetCourseIdList.map { NoticeTargetCourseEntity(
            notice = noticeEntity,
            course = CourseEntity(it)
        ) }
            .toMutableSet())

        noticeEntity.noticeSessions.plusAssign(sessions.map { NoticeSessionEntity(
            notice = noticeEntity,
            eventStartDateTime = it.sessionStartDateTime,
            eventEndDateTime = it.sessionEndDateTime,
        ) }
            .toMutableSet())

        return noticeEntity

    }

    /**
     * 세션이 있는 Notice 인지 확인
     */
    fun isSessionNotice(): Boolean {
        return listOf(NoticeType.EVENT, NoticeType.SPECIAL_LECTURE).contains(this.noticeType)
    }
}

/**
 * 공지사항 세션 DTO
 */
data class NoticeSessionDTO(
    @field:NotBlank(message = "startDate가 비어있습니다.")
    val sessionStartDateTime: LocalDateTime,

    @field:NotBlank(message = "endDate가 비어있습니다.")
    val sessionEndDateTime: LocalDateTime,
)

