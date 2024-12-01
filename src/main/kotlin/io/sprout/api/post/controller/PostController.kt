package io.sprout.api.post.controller

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {
    @PostMapping
    @Operation(
        summary = "프로젝트 등록 API",
        description = "프로젝트 및 스터디 모집 관련 정보를 받아 프로젝트를 생성하는 API입니다.", // 상세 설명
    )
    fun postProject(@RequestBody dto: ProjectRecruitmentRequestDto): ResponseEntity<Boolean> {
        val result = postService.createProjectPost(dto)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{postId}")
    @Operation(
        summary = "게시글 삭제 API",
        description = "특정 게시글을 삭제하는 API입니다."
    )
    fun deleteProject(@PathVariable postId: Long): ResponseEntity<Boolean> {
        val result = postService.deletePost(postId)
        return if (result) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().body(false)
        }
    }
}
