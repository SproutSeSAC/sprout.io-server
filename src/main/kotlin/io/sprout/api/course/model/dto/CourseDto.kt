package io.sprout.api.course.model.dto

import io.swagger.v3.oas.annotations.media.Schema

class CourseDto {

    @Schema(description = "campus 내 course 리스트 조회 response")
    data class CourseListResponse(
        val courseList: List<CourseDetail>
    ) {
        data class CourseDetail(
            val id: Long,
            val title: String,
            val campusName: String
        )
    }
}