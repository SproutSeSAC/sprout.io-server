package io.sprout.api.post.controller

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.swagger.v3.oas.annotations.Operation
import jakarta.persistence.Id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.*
import retrofit2.Response

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService
) {

    @Operation(summary = "공지사항 글 생성")
    @PostMapping("/notice")
    fun createNotice(@RequestBody noticeRequestDto: NoticeRequestDto): ResponseEntity<Any> {
        return try {
            val result = postService.createNotice(noticeRequestDto)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("공지사항 생성 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "프로젝트 글 생성")
    @PostMapping("/project")
    fun createProject(@RequestBody projectRecruitmentRequestDto: ProjectRecruitmentRequestDto): ResponseEntity<Any> {
        return try {
            val result = postService.createProject(projectRecruitmentRequestDto)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("프로젝트 생성 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    data class ErrorResponse(
        val message: String,
        val details: String
    )
}