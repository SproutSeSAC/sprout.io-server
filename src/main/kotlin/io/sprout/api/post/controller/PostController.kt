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
            summary = "게시글 등록 API",
            description = "공지사항 또는 프로젝트를 생성하는 API입니다. 입력 DTO의 타입에 따라 저장 데이터가 바뀝니다."
    )
    fun createPost(@RequestBody dto: Any): ResponseEntity<Boolean> {
        val result = when (dto) {
            is NoticeRequestDto -> postService.createNoticePost(dto)
            is ProjectRecruitmentRequestDto -> postService.createProjectPost(dto)
            else -> throw IllegalArgumentException("DTO 구성을 확인 해 주세요.")
        }
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
