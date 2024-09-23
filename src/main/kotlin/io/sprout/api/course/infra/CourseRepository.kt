package io.sprout.api.course.infra

import io.sprout.api.course.model.entities.CourseEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository: JpaRepository<CourseEntity, Long> {

    fun findCourseById(id: Long): CourseEntity?

    @EntityGraph(attributePaths = ["campus"])
    fun findByCampusId(campusId: Long): List<CourseEntity>
}