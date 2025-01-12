package io.sprout.api.mypage.repository

import io.sprout.api.user.model.entities.UserCourseEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCourseRepository : JpaRepository<UserCourseEntity, Long> {
    fun findByUser_Id(userId: Long): List<UserCourseEntity>
}