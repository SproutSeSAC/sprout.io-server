package io.sprout.api.post.controller

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/post")
class PostController(
    private val postService: PostService
) {

    @Operation(summary = "공지사항 글 생성")
    @PostMapping("/notice")
    fun createNotice(@RequestBody noticeRequestDto: NoticeRequestDto): ResponseEntity<PostEntity> {
        var result = postService.createNotice(noticeRequestDto)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "프로젝트 글 생성")
    @PostMapping("/project")
    fun createProject(@RequestBody projectRecruitmentRequestDto: ProjectRecruitmentRequestDto): ResponseEntity<PostEntity> {
        var result = postService.createProject(projectRecruitmentRequestDto)
        return ResponseEntity.ok(result)
    }
}