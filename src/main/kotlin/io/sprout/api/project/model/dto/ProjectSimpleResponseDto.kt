package io.sprout.api.project.model.dto

data class ProjectSimpleResponseDto(
    val projectId: Long,
    val title: String,
    val content: String,
    val userNickname: String,
    val imgUrl: String
)