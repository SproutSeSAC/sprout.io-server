package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType

data class UserSearchResponseDto(
    val userId: Long,
    val name: String,
    val nickname: String,
    val role: RoleType,
    val email: String,
    val phoneNumber: String,
    val campus: MutableList<Campus> = mutableListOf(),
    val course: MutableList<Course> = mutableListOf(),
){
    data class Campus(
        val campusId: Long,
        val name: String
    )

    data class Course(
        val courseId: Long,
        val name: String
    )

    fun distinctCampus() {
        val distinctCampus = campus.distinct()
        campus.clear()
        campus.addAll(distinctCampus)
    }

}
