package io.sprout.api.course.infra

import io.sprout.api.course.model.dto.CourseSearchRequestDto
import io.sprout.api.course.model.dto.CourseSearchResponseDto
import io.sprout.api.user.model.entities.UserEntity


interface CourseRepositoryCustom {
    fun searchCourse(searchRequest: CourseSearchRequestDto, requestUser: UserEntity): CourseSearchResponseDto
}