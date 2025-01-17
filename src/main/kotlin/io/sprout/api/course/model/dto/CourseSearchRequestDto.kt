package io.sprout.api.course.model.dto

data class CourseSearchRequestDto(
    val keyword: String?,
    val campusId: Long?,

    val page: Int = 1,
    val size: Int = 10
){
    val offset
        get() = (page-1) * size
}

