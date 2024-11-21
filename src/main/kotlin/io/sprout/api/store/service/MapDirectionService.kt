package io.sprout.api.store.service

import io.sprout.api.store.model.dto.DirectionResponse
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.Trafast

interface MapDirectionService {
    fun findDirection(directionRequest: StoreDto.MapDirectionRequest): DirectionResponse
}