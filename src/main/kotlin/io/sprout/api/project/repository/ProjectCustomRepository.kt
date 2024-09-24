package io.sprout.api.project.repository

import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto

interface ProjectCustomRepository {
    fun filterProjects(filterRequest: ProjectFilterRequest, userId: Long): Pair<List<ProjectResponseDto>, Long>
}