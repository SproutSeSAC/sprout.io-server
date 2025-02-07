package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType

data class RoleUpdateRequestDto(
    val courseIdList: MutableSet<Long> = mutableSetOf(),
    val campusIdList: MutableSet<Long> = mutableSetOf(),
    val role: RoleType
)
