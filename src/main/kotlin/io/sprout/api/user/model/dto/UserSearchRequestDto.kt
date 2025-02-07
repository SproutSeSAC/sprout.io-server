package io.sprout.api.user.model.dto

data class UserSearchRequestDto(
    val page: Long,
    val size: Long,
    val keyword: String?,
    val campusId: Long?,
    val courseId: Long?,
){
    fun getOffset():Long {
        return (page-1) * size
    }
}
