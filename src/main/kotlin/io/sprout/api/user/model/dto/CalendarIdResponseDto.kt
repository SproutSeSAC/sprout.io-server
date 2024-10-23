package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.GoogleCalendarEntity

data class CalendarIdResponseDto(
    var id : Long,
    var calendarId : Long,
    var userId : Long
){
    companion object {
        fun toDto(googleCalendarEntity: GoogleCalendarEntity): CalendarIdResponseDto {
            return CalendarIdResponseDto(
                id = googleCalendarEntity.id,
                calendarId = googleCalendarEntity.id, // calendarId가 실제로 id와 다른 값이면 수정 필요
                userId = googleCalendarEntity.user.id
            )
        }
    }
}