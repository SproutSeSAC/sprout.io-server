package io.sprout.api.project.repository

import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto

interface ProjectCustomRepository {
    fun filterProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long>
}