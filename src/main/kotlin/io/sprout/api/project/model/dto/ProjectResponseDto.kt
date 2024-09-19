package io.sprout.api.project.model.dto

import java.time.LocalDate

data class ProjectResponseDto (
    val id: Long,
    val title: String,
    val description: String,
    val recruitmentCount: Int,
    val meetingType: String,
    val contactMethod: String,
    val recruitmentStart: LocalDate,
    val recruitmentEnd: LocalDate
)