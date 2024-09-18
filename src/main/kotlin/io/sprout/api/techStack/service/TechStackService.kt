package io.sprout.api.techStack.service

import io.sprout.api.techStack.model.dto.TechStackResponseDto

interface TechStackService {
    fun getAllTechStack(): List<TechStackResponseDto>
}