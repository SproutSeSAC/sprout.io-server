package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

data class ManagerEmailResponseDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val name: String?,
    val roleType: RoleType
) {


    companion object {
        fun toDto(userEntity: UserEntity): ManagerEmailResponseDto {
            return ManagerEmailResponseDto(
                id = userEntity.id,
                email = userEntity.email, // calendarId가 실제로 id와 다른 값이면 수정 필요
                nickname = userEntity.nickname,
                name = userEntity.name ?: "NULL",
                roleType = userEntity.role,
            )
        }
    }
}

