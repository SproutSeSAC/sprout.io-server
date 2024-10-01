package io.sprout.api.project.repository

import io.sprout.api.project.model.dto.ProjectCommentResponseDto
import io.sprout.api.project.model.dto.ProjectDetailResponseDto
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto

interface ProjectCustomRepository {
    fun filterProjects(filterRequest: ProjectFilterRequest, userId: Long): Pair<List<ProjectResponseDto>, Long>
    fun findProjectDetailById(id: Long): ProjectDetailResponseDto?
    fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto>
}