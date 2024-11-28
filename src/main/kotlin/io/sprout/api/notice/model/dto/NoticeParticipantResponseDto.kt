package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.model.entities.ParticipantStatus
import java.time.LocalDateTime

/**
 * 세션 참가자 조회
 */
data class NoticeParticipantResponseDto(
    val userId: Long,
    val status: ParticipantStatus,
    val userName: String?,
    val nickName: String,
    val profileImageUrl: String?
//    val participants: List<Participant> = listOf()
) {
    constructor(participant: NoticeParticipantEntity) : this(
        participant.user.id,
        participant.status,
        participant.user.name,
        participant.user.nickname,
        participant.user.profileImageUrl
    )
}

data class Participant(
    val userId: Long,
    val status: ParticipantStatus,
    val userName: String,
    val nickName: String,
    val profileImageUrl: String
)

