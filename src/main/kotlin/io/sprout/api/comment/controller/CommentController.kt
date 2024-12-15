package io.sprout.api.comment.controller

import io.sprout.api.comment.dto.CommentRequestDto
import io.sprout.api.comment.entity.CommentEntity
import io.sprout.api.comment.service.CommentService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService
) {

    @Operation(summary = "댓글 추가")
    @PostMapping("/")
    fun addCommentToPost(
        @RequestBody dto: CommentRequestDto
    ): ResponseEntity<CommentEntity> {
        val comment = commentService.addCommentToPost(dto)
        return ResponseEntity.ok(comment)
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.noContent().build()
    }
}
