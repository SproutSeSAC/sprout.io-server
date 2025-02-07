package io.sprout.api.user.repository

import io.sprout.api.common.model.entities.PageResponse
import io.sprout.api.user.model.dto.UserSearchRequestDto
import io.sprout.api.user.model.dto.UserSearchResponseDto
import io.sprout.api.user.model.entities.UserEntity

interface UserRepositoryCustom {
    fun findManagerEmailSameCourse(courseId: Long): List<UserEntity>
    fun search(searchRequest: UserSearchRequestDto, user: UserEntity): PageResponse<UserSearchResponseDto>
}