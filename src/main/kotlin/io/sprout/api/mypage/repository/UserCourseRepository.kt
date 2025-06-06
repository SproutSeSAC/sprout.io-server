package io.sprout.api.mypage.repository

import io.sprout.api.user.model.entities.UserCourseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserCourseRepository : JpaRepository<UserCourseEntity, Long> {
    fun findByUserId(userId: Long): List<UserCourseEntity>
    fun deleteByCourseIdAndUserId(courseId: Long, id: Long)
    fun findAllByCourseId(courseId: Long): List<UserCourseEntity>
}