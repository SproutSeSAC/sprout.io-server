package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.GoogleCalendarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GoogleCalendarRepository : JpaRepository<GoogleCalendarEntity, Long> {
    fun findByCalendarId(calendarId: String): GoogleCalendarEntity?
    fun findByCourseId(courseId: Long): List<GoogleCalendarEntity>
}