package io.sprout.api.project.model.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class ProjectResponseDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val description: String,
    val recruitmentCount: Int,
    val meetingType: String,
    val contactMethod: String,
    val recruitmentStart: LocalDate,
    val recruitmentEnd: LocalDate,
    val pType: String,
    val positionNames: List<String>,
    val isScraped: Boolean,
    val viewCount: Int
)