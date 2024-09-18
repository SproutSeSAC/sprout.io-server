package io.sprout.api.project.controller

import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/project")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping
    @Operation(
        summary = "프로젝트 등록 API", // 간단한 설명
        description = "프로젝트 및 스터디 모집 관련 정보를 받아 프로젝트를 생성하는 API입니다.", // 상세 설명
    )
    fun postProject(@RequestBody dto: ProjectRecruitmentRequestDto): ResponseEntity<Boolean> {
        val result = projectService.postProject(dto)
        return ResponseEntity.ok(result)
    }
}