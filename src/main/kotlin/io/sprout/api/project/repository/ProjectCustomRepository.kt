package io.sprout.api.project.repository

import io.sprout.api.project.model.dto.*

interface ProjectCustomRepository {
    fun filterProjects(filterRequest: ProjectFilterRequest, userId: Long): Pair<List<ProjectResponseDto>, Long>
    fun findProjectDetailById(id: Long, userId: Long): ProjectDetailResponseDto?
    fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto>
    fun findProjectsEndingCloseWithDetails(size: Long, days: Long) : List<ProjectSimpleResponseDto>
    fun findAllByIdIn(ids: List<Long>): Pair<List<ProjectResponseDto>, Long>
}