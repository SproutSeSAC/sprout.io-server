package io.sprout.api.course.model.dto

import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDate

data class CourseSearchResponseDto(
    val courses: List<CourseListViewDto> = mutableListOf()
){

    data class CourseListViewDto(
        val courseId: Long,
        val campus: String,
        val name: String,
        val startDate: LocalDate,
        val endDate: LocalDate,
    ){
        var courseManager: MutableList<CampusManagerDto> = mutableListOf()
    }

    data class CampusManagerDto(
        val userId: Long,
        val userName: String,
        val role: RoleType
    )

    data class EduManagerProjection(
        val courseId: Long,
        val eduManagers: MutableList<CampusManagerDto>
    )
}