package io.sprout.api.project.service

import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.model.dto.ProjectResponseDto

interface ProjectService {
    fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean
    fun getFilteredProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long>
    fun toggleScrapProject(userId: Long, projectId: Long): Boolean
    fun increaseViewCount(projectId: Long): Boolean
}