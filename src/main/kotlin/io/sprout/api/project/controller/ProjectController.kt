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
        @RequestParam(required = false) position: List<Long>?,
        @RequestParam(required = false) meetingType: String?
    ): ResponseEntity<Map<String, Any>> {
        val filterRequest = ProjectFilterRequest(techStack, position, meetingType)

        // 서비스에서 Pair<List<ProjectResponseDto>, Long> 형태의 결과를 받음
        val (filteredProjects, totalCount) = projectService.getFilteredProjects(filterRequest)

        // 결과를 Map 형태로 변환하여 반환
        val responseBody = mapOf(
            "projects" to filteredProjects,
            "totalCount" to totalCount
        )

        return ResponseEntity.ok(responseBody)
    }


}