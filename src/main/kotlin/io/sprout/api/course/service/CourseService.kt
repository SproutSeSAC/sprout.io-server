package io.sprout.api.course.service

import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.course.model.dto.CourseDto
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository
) {

    fun getCourseListByCampusId(campusId: Long): CourseDto.CourseListResponse {
        val courseList = courseRepository.findByCampusId(campusId).sortedBy { it.id }
        val response = courseList.map { course ->
            CourseDto.CourseListResponse.CourseDetail(
                id = course.id,
                title = course.title,
                campusName = course.campus!!.name
            )
        }

        return CourseDto.CourseListResponse(
            courseList = response
        )
    }
}