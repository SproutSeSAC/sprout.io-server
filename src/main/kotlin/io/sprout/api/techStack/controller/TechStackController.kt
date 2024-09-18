package io.sprout.api.techStack.controller

import io.sprout.api.techStack.model.dto.TechStackResponseDto
import io.sprout.api.techStack.service.TechStackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/techStack")
class TechStackController(
    private val techStackService: TechStackService
) {
    @GetMapping
    fun getAllTechStack() : ResponseEntity<List<TechStackResponseDto>>{
        val result = techStackService.getAllTechStack()
        return ResponseEntity.ok(result)
    }
}