package io.sprout.api.post.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.comment.dto.CommentResponseDto
import io.sprout.api.comment.service.CommentService
import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.post.dto.PostRequestDto
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService,
    private val commentService: CommentService,
    private val securityManager: SecurityManager,
    private val objectMapper: ObjectMapper,
) {
    @PostMapping
    @Operation(
            summary = "게시글 등록 API",
            description = "공지사항 또는 프로젝트를 생성하는 API입니다. 입력 DTO의 타입에 따라 저장 데이터가 바뀝니다."
    )
    fun createPost(@RequestBody requestMap: Map<String, Any>): ResponseEntity<Pair<Long, Long>> {
        val clientID = securityManager.getAuthenticatedUserName()
            ?: return ResponseEntity.status(401).build()

        return try {
            if (requestMap.containsKey("projectTitle")) {
                val dto = objectMapper.convertValue(requestMap, ProjectRecruitmentRequestDto::class.java)
                val result = postService.createProjectPost(dto, clientID)
                ResponseEntity.ok(result)
            } else if (requestMap.containsKey("noticeType")) {
                val dto = objectMapper.convertValue(requestMap, NoticeRequestDto::class.java)
                val result = postService.createNoticePost(dto, clientID)
                ResponseEntity.ok(result)
            } else if (requestMap.containsKey("storeName")) {
                val dto = objectMapper.convertValue(requestMap, MealPostDto.MealPostCreateRequest::class.java)
                val result = postService.createMealPost(dto, clientID)
                ResponseEntity.ok(result)
            } else {
                throw IllegalArgumentException("DTO 구성을 확인 해 주세요.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 상세 조회 API",
            description = "게시글을 상세 조회하는 API입니다. ID를 입력받아 반환합니다."
    )
    fun getPostById(@PathVariable postId: Long): ResponseEntity<Any> {
        val responseDto = postService.getPostById(postId)
        return ResponseEntity.ok(responseDto)
    }

    @GetMapping("/type/{posttype}")
    @Operation(
            summary = "게시글 목록 조회 API",
            description = "compact=true 시 제목, 내용, ID만 반환합니다. (프로젝트는 내용 대신 작성자 ID를 반환합니다.)"
    )
    fun getPostsByType(
            @PathVariable posttype: String,
            @RequestParam(required = false, defaultValue = "false") compact: Boolean
    ): ResponseEntity<List<Any>> {
        val postType = try {
            PostType.valueOf(posttype.uppercase())
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(emptyList())
        }

        val posts = postService.getPostsByPostType(postType, compact)
        return ResponseEntity.ok(posts)
    }

    @PutMapping("/{postId}")
    @Operation(
            summary = "게시글 수정 API",
            description = "입력된 DTO 타입에 따라 공지사항 또는 프로젝트 게시글을 수정합니다."
    )
    fun updatePost(@PathVariable postId: Long, @RequestBody requestMap: Map<String, Any>): ResponseEntity<Boolean> {
        val clientID = securityManager.getAuthenticatedUserName()
            ?: return ResponseEntity.status(401).build()

        return try {
            if (requestMap.containsKey("projectTitle")) {
                val dto = objectMapper.convertValue(requestMap, ProjectRecruitmentRequestDto::class.java)
                val result = postService.updatePost(postId, dto)
                ResponseEntity.ok(result)
            } else if (requestMap.containsKey("noticeType")) {
                val dto = objectMapper.convertValue(requestMap, NoticeRequestDto::class.java)
                val result = postService.updatePost(postId, dto)
                ResponseEntity.ok(result)
            } else if (requestMap.containsKey("storeName")) {
                val dto = objectMapper.convertValue(requestMap, MealPostDto.MealPostCreateRequest::class.java)
                val result = postService.updatePost(postId, dto)
                ResponseEntity.ok(result)
            } else {
                throw IllegalArgumentException("DTO 구성을 확인 해 주세요.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(
        summary = "게시글 삭제 API",
        description = "특정 게시글을 삭제하는 API입니다."
    )
    fun deletePost(@PathVariable postId: Long): ResponseEntity<Boolean> {
        val result = postService.deletePost(postId)
        return if (result) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.badRequest().body(false)
        }
    }

    @GetMapping("/{postId}/comments")
    @Operation(
            summary = "특정 게시글 댓글 조회 API",
            description = "해당 postId에 달린 모든 댓글을 조회합니다."
    )
    fun getCommentsByPostId(@PathVariable postId: Long): ResponseEntity<List<CommentResponseDto>> {
        val comments = commentService.getCommentsByPostId(postId)
        return ResponseEntity.ok(comments)
    }

    @GetMapping("/{postId}/link")
    @Operation(
            summary = "게시글 Linked ID 조회 API",
            description = "특정 게시글의 Linked ID를 반환합니다."
    )
    fun getLinkedIdByPostId(@PathVariable postId: Long): ResponseEntity<Long> {
        val linkedId = postService.getLinkedIdByPostId(postId)
        return ResponseEntity.ok(linkedId)
    }
}
