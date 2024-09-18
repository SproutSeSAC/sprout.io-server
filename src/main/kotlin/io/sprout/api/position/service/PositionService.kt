package io.sprout.api.position.service

import io.sprout.api.position.model.dto.PositionResponseDto
import io.sprout.api.position.repository.PositionRepository

class PositionService (
    private val positionRepository: PositionRepository
){
    fun getPosition(): List<PositionResponseDto> {
       return positionRepository.findAll().map { it.toDto() }
    }
}