package io.sprout.api.project.repository

import io.sprout.api.project.model.dto.*
import java.time.LocalDate

interface ProjectCustomRepository {
    fun filterProjects(filterRequest: ProjectFilterRequest, userId: Long): Pair<List<ProjectResponseDto>, Long>
    fun findProjectDetailById(id: Long, userId: Long): ProjectDetailResponseDto?
    fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto>
    fun findProjectsEndingTommorowWithDetails(yesterday: LocalDate) : List<ProjectSimpleResponseDto>
}