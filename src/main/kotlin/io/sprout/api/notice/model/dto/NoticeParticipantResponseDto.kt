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
    val userName: String?,
    val nickName: String,
    val profileImageUrl: String?
) {
    constructor(participant: NoticeParticipantEntity) : this(
        participant.id,
        participant.user.id,
        participant.status,
        participant.user.phoneNumber,
        participant.user.name,
        participant.user.nickname,
        participant.user.profileImageUrl
    )
}


