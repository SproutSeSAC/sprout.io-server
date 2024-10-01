package io.sprout.api.project.controller

import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.PType
import io.sprout.api.project.model.entities.ProjectCommentEntity
import io.sprout.api.project.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.jpa.domain.AbstractPersistable_.id
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

    @GetMapping()
    @Operation(summary = "프로젝트 조회 필터 API", description = "프로젝트 조회 필터에 관한 API 입니다.")
    fun filterProjects(
        @RequestParam(required = false) techStack: List<Long>?,
        @RequestParam(required = false) position: List<Long>?,
        @RequestParam(required = false) meetingType: String?,
        @RequestParam(defaultValue = "1") page: Int,     // 기본값 1 설정
        @RequestParam(defaultValue = "20") size: Int,    // 기본값 20 설정
        @RequestParam(defaultValue = "false") onlyScraped: Boolean,
        @RequestParam(required = false) pType: PType?,
        @RequestParam(defaultValue = "latest") sort: String,
        @RequestParam(required = false) keyWord: String?,
    ): ResponseEntity<Map<String, Any?>> {
        val filterRequest = ProjectFilterRequest(
            techStack = techStack,
            position = position,
            meetingType = meetingType,
            page = page,
            size = size,
            onlyScraped = onlyScraped,
            pType = pType,
            sort = sort,
            keyWord = keyWord
        )

        val (filteredProjects, totalCount) = projectService.getFilteredProjects(filterRequest)

        val totalPages = (totalCount + filterRequest.size - 1) / filterRequest.size
        val nextPage = if (filterRequest.page + 1 < totalPages) filterRequest.page + 2 else null

        val responseBody = mapOf(
            "projects" to filteredProjects,
            "totalCount" to totalCount,
            "currentPage" to filterRequest.page + 1,
            "pageSize" to filterRequest.size,
            "totalPages" to totalPages,
            "nextPage" to nextPage
        )

        return ResponseEntity.ok(responseBody)
    }


    @Operation(
        summary = "프로젝트 스크랩/스크랩 취소 API", // 간단한 설명
        description = "사용자가 특정 프로젝트를 스크랩 또는 스크랩을 취소하는 API입니다. false 반환 -> 스크랩 취소됨 , true 반환 -> 스크랩 됨", // 상세 설명
    )
    @PostMapping("/{projectId}/scrap")
    fun toggleScrapProject(
        @PathVariable projectId: Long,
    ): ResponseEntity<Boolean> {
        val result = projectService.toggleScrapProject(projectId)
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "프로젝트 조회수 증가 API", // 간단한 설명
        description = "특정 프로젝트의 조회수를 증가시키는 API입니다. 1 증가시 true 반환", // 상세 설명
    )
    @PostMapping("/{projectId}/view")
    fun increaseViewCount(@PathVariable projectId: Long): ResponseEntity<Boolean> {
        val result = projectService.increaseViewCount(projectId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{projectId}")
    @Operation(
        summary = "프로젝트 상세 조회 API", // 간단한 설명
        description = "특정 프로젝트의 상세 정보를 조회하는 API입니다.", // 상세 설명
    )
    fun getProjectDetail(@PathVariable projectId: Long): ResponseEntity<ProjectDetailResponseDto?> {
        val projectDetail = projectService.findProjectDetailById(projectId)
        return if (projectDetail != null) {
            ResponseEntity.ok(projectDetail)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{projectId}/comment")
    @Operation(
        summary = "프로젝트 댓글 조회 API",
        description = "특정 프로젝트에 달린 모든 댓글을 조회하는 API입니다."
    )
    fun getComments(@PathVariable projectId: Long): ResponseEntity<List<ProjectCommentResponseDto>> {
        val comments = projectService.getCommentsByProjectId(projectId)
        return ResponseEntity.ok(comments)
    }


    @PostMapping("/{projectId}/comment")
    @Operation(
        summary = "프로젝트 댓글 등록 API",
        description = "특정 프로젝트에 댓글을 작성하는 API입니다."
    )
    fun postComment(
        @PathVariable projectId: Long,
        @RequestBody projectCommentRequestDto: ProjectCommentRequestDto
    ): ResponseEntity<Boolean> {
        println("content:${projectCommentRequestDto.content}")
        val result = projectService.postComment(projectId,projectCommentRequestDto.content)
        return if (result) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().body(false)
        }
    }
}