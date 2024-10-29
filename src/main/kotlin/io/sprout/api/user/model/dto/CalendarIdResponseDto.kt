package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.GoogleCalendarEntity

data class CalendarIdResponseDto(
    var id : Long,
    var calendarId : String,
    var registerId : Long,
    var courseId : Long,
){
    companion object {
        fun toDto(googleCalendarEntity: GoogleCalendarEntity): CalendarIdResponseDto {
            return CalendarIdResponseDto(
                id = googleCalendarEntity.id,
                calendarId = googleCalendarEntity.calendarId, // calendarId가 실제로 id와 다른 값이면 수정 필요
                registerId = googleCalendarEntity.user.id,
                courseId = googleCalendarEntity.courseId
            )
        }
    }
}