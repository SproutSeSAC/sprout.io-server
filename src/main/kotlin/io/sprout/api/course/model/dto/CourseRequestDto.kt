package io.sprout.api.course.model.dto

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.course.model.entities.CourseEntity
import java.time.LocalDate

data class CourseRequestDto(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val title: String,
    val campusId: Long
){
    fun toEntity(): CourseEntity {
        return CourseEntity(
            title = title,
            startDate= startDate,
            endDate = endDate,
            CampusEntity(campusId),
            calendarId = ""
        )
    }
}

