package io.sprout.api.user.service

import io.sprout.api.user.model.dto.CalendarIdResponseDto
import io.sprout.api.user.model.entities.GoogleTokenEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

interface GoogleUserService {
    fun saveOrUpdateToken(user: UserEntity, accessToken: String, refreshToken: String?, expiresIn: Int)
    fun getTokenByUser(user: UserEntity): GoogleTokenEntity?
    fun refreshAccessToken(user: UserEntity): GoogleTokenEntity?
    fun registerGoogleCalendarId(calendarId: String) : Boolean
    fun getCalendarIdWithManagerGroup(roleType: RoleType): List<CalendarIdResponseDto>
}