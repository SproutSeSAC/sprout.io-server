package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType

data class UserSearchRequestDto(
    val page: Long,
    val size: Long,
    val keyword: String?,
    val campusId: Long?,
    val courseId: Long?,
    var roles: List<RoleType>?
){
    fun getOffset():Long {
        return (page) * size
    }
}
