package io.sprout.api.project.service

import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto

interface ProjectService {
    fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean
}