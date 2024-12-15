package io.sprout.api.post.controller

import io.sprout.api.comment.dto.CommentResponseDto
import io.sprout.api.comment.service.CommentService
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.swagger.v3.oas.annotations.Operation
import org.apache.naming.ResourceRef
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService,
    private val commentService: CommentService
) {

    @Operation(summary = "상세 글 ID 읽기")
    @GetMapping("/gerorigin/{post_id}")
    fun getRefInfo(@PathVariable post_id: Long): ResponseEntity<Any> {
        return try {
            val result = postService.getOrigin(post_id)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("ID 읽기 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

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

    @Operation(summary = "글 삭제")
    @DeleteMapping("/remove")
    fun deletePost(@RequestBody post_id: Long): ResponseEntity<Any> {
        return try {
            val result = postService.deletePost(post_id)
            ResponseEntity.ok(result);
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("글 삭제 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "글 조회")
    @GetMapping("/get/{post_id}")
    fun getPost(@PathVariable post_id: Long): ResponseEntity<Any> {
        return try {
            val result = postService.getPost(post_id)
            ResponseEntity.ok(result)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("글 조회 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "글들 조회 (타입 기준)")
    @GetMapping("/get/{postType}")
    fun getPostsByType(@PathVariable postType: String): ResponseEntity<Any> {
        return try {
            val posts = postService.getPostsByType(postType)
            ResponseEntity.ok(posts)
        } catch (e: Exception) {
            val errorResponse = ErrorResponse("글 목록 조회 실패", e.message ?: "로그 확인")
            ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "댓글 조회")
    @GetMapping("/{postId}/comments")
    fun getCommentsByPostId(@PathVariable postId: Long): ResponseEntity<List<CommentResponseDto>> {
        val comments = commentService.getCommentsByPostId(postId)
        return ResponseEntity.ok(comments)
    }

    data class ErrorResponse(
        val error_message: String,
        val details: String
    )
}