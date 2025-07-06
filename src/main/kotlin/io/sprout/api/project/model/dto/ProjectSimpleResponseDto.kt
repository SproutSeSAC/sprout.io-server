package io.sprout.api.project.model.dto

data class ProjectSimpleResponseDto(
    val postId: Long?,
    val projectId: Long?,
    val title: String,
    val content: String,
    val userNickname: String,
    val imgUrl: String,
    val postId: Long
)