package io.sprout.api.project.controller

import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @Operation(
        summary = "프로젝트 조회 필터 API", // 간단한 설명
        description = "프로젝트 조회 필터에 관한 API 입니다.", // 상세 설명
    )
    @GetMapping
    fun filterProjects(
        @RequestParam(required = false) techStack: List<Long>?,
        @RequestParam(required = false) position: String?,
        @RequestParam(required = false) meetingType: String?
    ): ResponseEntity<List<ProjectResponseDto>> {
        val filterRequest = ProjectFilterRequest(techStack, position, meetingType)
        val filteredProjects = projectService.getFilteredProjects(filterRequest)
        return ResponseEntity.ok(filteredProjects)
    }

}