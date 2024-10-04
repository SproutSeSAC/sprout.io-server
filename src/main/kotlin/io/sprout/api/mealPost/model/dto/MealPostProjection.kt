package io.sprout.api.mealPost.model.dto

import java.time.LocalDateTime

interface MealPostProjection {
    val id: Long
    val title: String
    val appointmentTime: LocalDateTime
    val memberCount: Int
    val meetingPlace: String
    val ordinalNumber: Int?
}