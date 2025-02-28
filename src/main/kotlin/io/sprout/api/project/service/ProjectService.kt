package io.sprout.api.project.service

import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.ProjectCommentEntity
import io.sprout.api.project.model.entities.ProjectEntity

interface ProjectService {
    fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean
    fun getFilteredProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long>
    fun toggleScrapProject(projectId: Long): Boolean
    fun increaseViewCount(projectId: Long): Boolean
    fun findProjectDetailById(projectId: Long): ProjectDetailResponseDto?
    fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto>
    fun postComment(projectId: Long, content: String): Boolean
    fun deleteComment(commentId: Long): Boolean
    fun deleteProject(projectId: Long): Boolean
    fun updateProject(projectId: Long, projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean
    fun getProjectsEndingTomorrow(): List<ProjectSimpleResponseDto>
    fun postProjectAndGetId(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Long
    fun getProjectTitleById(linkedId: Long): String
    fun getProjectById(linkedId: Long): ProjectEntity?
}