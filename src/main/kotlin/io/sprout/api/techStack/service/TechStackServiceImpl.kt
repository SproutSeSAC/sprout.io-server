package io.sprout.api.techStack.service

import io.sprout.api.techStack.model.dto.TechStackResponseDto
import io.sprout.api.techStack.repository.TechStackRepository
import org.springframework.stereotype.Service

@Service
class TechStackServiceImpl(
    private val techStackRepository: TechStackRepository
) : TechStackService {
    override fun getAllTechStack(): List<TechStackResponseDto> {
       return techStackRepository.findAll().map { it.toDto() }.toList()
    }
}