package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.UserEntity

interface UserRepositoryCustom {
    fun findManagerEmailSameCourse(courseId: Long): List<UserEntity>
}