package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.model.entities.ParticipantStatus

/**
 * 세션 참가자 조회
 */
data class NoticeParticipantResponseDto(
    val noticeParticipantId: Long,
    val userId: Long,
    val status: ParticipantStatus,
    val phoneNumber: String?,
    val email: String,
    val userName: String?,
    val nickName: String,
    val profileImageUrl: String?,

    val courses: List<Course>,
    val campuses: List<Campus>
) {
    constructor(participant: NoticeParticipantEntity) : this(
        participant.id,
        participant.user.id,
        participant.status,
        participant.user.phoneNumber,
        participant.user.email,
        participant.user.name,
        participant.user.nickname,
        participant.user.profileImageUrl,

        participant.user.userCourseList
            .map { Course(it.course.id, it.course.title) },
        participant.user.userCourseList
            .map { Campus(it.course.campus.id, it.course.campus.name) }
            .distinct()
    )

    data class Course(
        val id: Long,
        val name: String
    )
    data class Campus(
        val id: Long,
        val name: String
    )


}


