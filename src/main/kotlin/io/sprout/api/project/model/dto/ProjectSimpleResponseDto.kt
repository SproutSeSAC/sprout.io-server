package io.sprout.api.project.model.dto

data class ProjectSimpleResponseDto(
    val projectId: Long,
    val content: String,
    val userNickname: String
)