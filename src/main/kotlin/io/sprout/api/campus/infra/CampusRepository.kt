package io.sprout.api.campus.infra

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.course.model.entities.CourseEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampusRepository: JpaRepository<CampusEntity, Long> {

//    @EntityGraph(attributePaths = ["course"])
//    fun findCourseById(campusId: Long): CampusEntity?
}