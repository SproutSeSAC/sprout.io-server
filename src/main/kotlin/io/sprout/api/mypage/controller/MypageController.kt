package io.sprout.api.mypage.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.service.MypageService
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mypage")
class MypageController(
        private val mypageService: MypageService,
        private val securityManager: SecurityManager
) {

    @Operation(summary = "프로필 조회", description = "프로필 카드 조회 API, 프로필과 교육과정 카드에 필요한 정보를 반환합니다.")
    @GetMapping("/getCard")
    fun getUserCard(): ResponseEntity<CardDto.UserCard> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(null)
        return ResponseEntity.ok(mypageService.getUserCard(userId))
    }

    @Operation(summary = "닉네임 업데이트", description = "새로운 닉네임을 입력하여 수정할 수 있습니다.")
    @PatchMapping("/updateNickname")
    fun updateNickname(@RequestBody request: UpdateNickNameDto): ResponseEntity<String> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")
        mypageService.updateNickname(userId, request)
        return ResponseEntity.ok("닉네임이 업데이트되었습니다.")
    }

    @Operation(summary = "프사 업데이트", description = "새로운 프사 Url로 수정할 수 있습니다.")
    @PatchMapping("/updateProfileUrl")
    fun updateProfileUrl(@RequestBody request: UpdateProfileUrlDto): ResponseEntity<String> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body("로그인이 필요합니다.")
        mypageService.updateProfileUrl(userId, request)
        return ResponseEntity.ok("프사가 업데이트되었습니다.")
    }

    @Operation(summary = "작성 글 조회", description = "작성한 글들의 ID를 반환합니다.")
    @GetMapping("/getPost")
    fun getPostList(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false) postTypes: List<PostType>?
    ): ResponseEntity<Page<PostAndNickNameDto>> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(null)
        return ResponseEntity.ok(mypageService.getPostListByUserId(userId, pageable, postTypes))
    }

    @Operation(summary = "작성 댓글 조회", description = "작성한 댓글들의 ID와 게시글 ID를 반환합니다.")
    @GetMapping("/getComments")
    fun getCommentList(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false) postTypes: List<PostType>?
    ): ResponseEntity<Page<PostCommentDto>> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(null)
        return ResponseEntity.ok(mypageService.getPostCommentListByUserId(userId, pageable, postTypes))
    }

    @Operation(summary = "찜한 글 조회", description = "찜한 글들의 ID를 반환합니다.")
    @GetMapping("/getScrap")
    fun getPostScrapList(
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<Page<GetPostResponseDto>> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(null)
        return ResponseEntity.ok(mypageService.getPostScrapListByUserId(userId, pageable))
    }

    @Operation(summary = "신청한 글 조회 (제목만)", description = "신청한 글의 제목들을 반환합니다.")
    @GetMapping("/getParticipantTitle")
    fun getPostParticipantIDList(): ResponseEntity<List<String>> {
        val userId = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).body(null)

        return ResponseEntity.ok(mypageService.getPostParticipantListTitleByUserId(userId))
    }

    @Operation(summary = "신청한 글 상세정보", description = "신청한 글들의 정보를 반환합니다.")
    @GetMapping("/getParticipant")
    fun getPostParticipantList(): ResponseEntity<ParticipantListResponseDto> {
        val userId = securityManager.getAuthenticatedUserName()
            ?: return ResponseEntity.status(401).body(null)

        return ResponseEntity.ok(mypageService.getPostParticipantListByUserId(userId))
    }
}
