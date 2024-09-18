package io.sprout.api.position.service

import io.sprout.api.position.model.dto.PositionResponseDto
import io.sprout.api.position.repository.PositionRepository
import org.springframework.stereotype.Service

@Service
class PositionServiceImpl (
    private val positionRepository: PositionRepository
) : PositionService {
    override fun getAllPositions(): List<PositionResponseDto> {
       return positionRepository.findAll().map { it.toDto() }.toList()
    }
}