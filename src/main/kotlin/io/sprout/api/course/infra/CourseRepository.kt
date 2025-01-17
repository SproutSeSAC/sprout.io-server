package io.sprout.api.course.infra

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.course.model.entities.CourseEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository: JpaRepository<CourseEntity, Long>, CourseRepositoryCustom {

    fun findCourseById(id: Long): CourseEntity?

    @EntityGraph(attributePaths = ["campus"])
    fun findByCampusId(campusId: Long): List<CourseEntity>

    @Query("SELECT course.campus " +
            "FROM CourseEntity course " +
            "LEFT JOIN course.userCourseList userCourse " +
            "LEFT JOIN course.campus campus " +
            "WHERE userCourse.user.id = :userId")
    fun findUserCampusByUserId(userId: Long): List<CampusEntity>
}