package io.sprout.api.project.service

import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDTO

interface ProjectService {
    fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDTO): Boolean
}