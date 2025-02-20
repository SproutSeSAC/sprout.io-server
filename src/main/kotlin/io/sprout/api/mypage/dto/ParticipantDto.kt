package io.sprout.api.mypage.dto

import java.time.LocalDateTime

data class ParticipantDto (
    val title: String,
    val id: Long,
    val participantId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)