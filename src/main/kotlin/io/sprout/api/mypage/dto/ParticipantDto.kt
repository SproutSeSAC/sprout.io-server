package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class ParticipantListResponseDto(
    val nearList: List<ParticipantDto>,
    val allList: List<ParticipantDto>
)

data class ParticipantDto (
    val title: String,
    val id: Long,
    val ordinal: Int,
    val participantId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)