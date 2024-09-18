package io.sprout.api.position.service

import io.sprout.api.position.model.dto.PositionResponseDto

interface PositionService {
    fun getAllPositions(): List<PositionResponseDto>
}