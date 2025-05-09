package io.sprout.api.mypage.dto

import io.sprout.api.notice.model.entities.NoticeMeetingType
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime

data class ParticipantListResponseDto(
    val nearList: List<ParticipantDto>,
    val allList: List<ParticipantDto>
)

data class ParticipantDto (
    val postId: Long,
//    val id: Long,
    val sessionId: Long,
    val title: String,
    val role: RoleType,
    val ordinal: Int,
    val participantId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val meetingType: NoticeMeetingType?,
    val meetingPlace: String?,
    val satisfactionSurvey: String?,
)