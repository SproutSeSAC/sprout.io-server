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
        @RequestParam(required = false) meetingType: String?,
        @RequestParam(defaultValue = "1") page: Int,  // 기본값 1 (프론트에서 1 기반 페이지 요청)
        @RequestParam(defaultValue = "20") size: Int  // 기본값 20 (한 페이지당 20개 데이터)
    ): ResponseEntity<Map<String, Any?>> {
        // 프론트에서 받은 page는 1부터 시작하므로, 내부적으로는 0부터 시작하도록 처리
        val pageIndex = page - 1
        val filterRequest = ProjectFilterRequest(techStack, position, meetingType, pageIndex, size)

        // 서비스에서 Pair<List<ProjectResponseDto>, Long> 형태의 결과를 받음
        val (filteredProjects, totalCount) = projectService.getFilteredProjects(filterRequest)

        // 전체 페이지 수 계산
        val totalPages = if (totalCount % size == 0L) {
            totalCount / size
        } else {
            totalCount / size + 1
        }

        // nextPage가 있을지 여부 계산 (현재 페이지가 마지막 페이지가 아닌 경우)
        val nextPage = if (page < totalPages) page + 1 else null

        // 결과를 Map 형태로 변환하여 반환
        val responseBody = mapOf(
            "projects" to filteredProjects,
            "totalCount" to totalCount,
            "currentPage" to page,  // 프론트에서 보낸 페이지 번호 반환 (1부터 시작)
            "pageSize" to size,
            "totalPages" to totalPages,  // 총 페이지 수
            "nextPage" to nextPage  // 다음 페이지 (마지막 페이지일 경우 null)
        )

        return ResponseEntity.ok(responseBody)
    }



}