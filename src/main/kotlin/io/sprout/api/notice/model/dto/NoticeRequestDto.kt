package io.sprout.api.notice.model.dto

import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.notice.model.entities.*
import io.sprout.api.user.model.entities.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
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

    @Schema(description = "등록 시작 시간", example = "2024-11-30T00:00:00", type = "string")
    val applicationStartDateTime: LocalDateTime?,

    @Schema(description = "등록 마감 시간", example = "2024-11-31T00:00:00", type = "string")
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

        noticeEntity.noticeSessions.plusAssign(sessions.sortedBy { it.sessionEndDateTime }.mapIndexed { index, noticeSessionDTO ->
            NoticeSessionEntity(
            notice = noticeEntity,
            eventStartDateTime = noticeSessionDTO.sessionStartDateTime,
            eventEndDateTime = noticeSessionDTO.sessionEndDateTime,
            ordinal = index+1
        ) }
            .toMutableSet())

        return noticeEntity

    }

    /**
     * 세션이 있는 Notice 인지 확인
     */
    fun addIsSessionNotice(): Boolean {
        return listOf(NoticeType.EVENT, NoticeType.SPECIAL_LECTURE).contains(this.noticeType)
    }
}

/**
 * 공지사항 세션 DTO
 */
data class NoticeSessionDTO(
    @field:NotBlank(message = "startDate가 비어있습니다.")
    @Schema(description = "세션 시작 시간", example = "2024-11-30T00:00:00", type = "string")
    val sessionStartDateTime: LocalDateTime,

    @field:NotBlank(message = "endDate가 비어있습니다.")
    @Schema(description = "세션 종료 시간", example = "2024-11-30T00:00:00", type = "string")
    val sessionEndDateTime: LocalDateTime,
)

