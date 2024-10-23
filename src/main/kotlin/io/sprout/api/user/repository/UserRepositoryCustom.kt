package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.GoogleCalendarEntity
import io.sprout.api.user.model.entities.RoleType
import org.springframework.data.repository.query.Param

interface UserRepositoryCustom {
    fun findUsersWithCalendarByRole(@Param("roleType") roleType: RoleType): List<GoogleCalendarEntity>
}