package io.sprout.api.mealPost.model.dto

import java.time.LocalDateTime

data class MealPostProjection (
    val id: Long,
    val title: String,
    val appointmentTime: LocalDateTime,
    val storeName: String,
    val meetingPlace: String,
    val targetMemberCount: Int,
    val currentMemberCount: Long,

    val ownerNickname: String,
    val ownerProfileImageUrl: String,

    val isParticipant: Boolean?
)