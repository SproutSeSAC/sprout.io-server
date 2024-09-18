package io.sprout.api.position.controller

import io.sprout.api.position.model.dto.PositionResponseDto
import io.sprout.api.position.service.PositionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/positions")
class PositionController(
    private val positionService: PositionService) {

    @GetMapping
    fun getAllPositions(): ResponseEntity<List<PositionResponseDto>>{
        val result = positionService.getAllPositions()
        return ResponseEntity.ok(result)
    }
}