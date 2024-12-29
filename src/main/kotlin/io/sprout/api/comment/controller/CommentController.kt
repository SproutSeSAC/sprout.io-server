package io.sprout.api.comment.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.comment.dto.CommentRequestDto
import io.sprout.api.comment.dto.CommentResponseDto
import io.sprout.api.comment.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
        private val commentService: CommentService,
        private val securityManager: SecurityManager
) {
    @PostMapping
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    fun createComment(@RequestBody dto: CommentRequestDto): ResponseEntity<CommentResponseDto> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()
        val response = commentService.createComment(clientID, dto)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "댓글 조회", description = "댓글 ID로 조회합니다.")
    fun getCommentById(@PathVariable commentId: Long): ResponseEntity<CommentResponseDto> {
        val response = commentService.getCommentById(commentId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "전체 댓글 조회", description = "모든 댓글을 조회합니다.")
    fun getAllComments(): ResponseEntity<List<CommentResponseDto>> {
        val responses = commentService.getAllComments()
        return ResponseEntity.ok(responses)
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 ID를 통해 해당 댓글을 수정합니다.")
    fun updateComment(
            @PathVariable commentId: Long,
            @RequestBody dto: CommentRequestDto
    ): ResponseEntity<CommentResponseDto> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()
        val response = commentService.updateComment(clientID, commentId, dto)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 ID로 해당 댓글을 삭제합니다.")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<Boolean> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(false)
        val result = commentService.deleteComment(clientID, commentId)
        return if (result) ResponseEntity.ok(true) else ResponseEntity.badRequest().body(false)
    }
}
