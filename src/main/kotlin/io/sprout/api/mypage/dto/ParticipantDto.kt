package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class nearParticipantDto(
    val title: String,
    val id: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)

data class ParticipantDto (
    val title: String,
    val id: Long,
    val participantId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val nearParticipant: MutableList<nearParticipantDto>
)