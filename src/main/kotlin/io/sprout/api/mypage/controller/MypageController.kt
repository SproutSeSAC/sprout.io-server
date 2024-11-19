package io.sprout.api.mypage.controller

import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.service.MypageService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mypage")
class MypageController(
        private val mypageService: MypageService
) {

    @Operation(summary = "프로필 조회", description = "프로필 카드 조회 API, 프로필과 교육과정 카드에 필요한 정보를 반환합니다.")
    @GetMapping("/getCard/{userId}")
    fun getUserCard(@PathVariable userId: Long): CardDto.UserCard {
        return mypageService.getUserCard(userId)
    }

    @Operation(summary = "닉네임 업데이트", description = "새로운 닉네임을 입력하여 수정할 수 있습니다.")
    @PatchMapping("/updateNickname/{userId}")
    fun updateNickname(
            @PathVariable userId: Long,
            @RequestBody request: UpdateNickNameDto
    ) {
        mypageService.updateNickname(userId, request)
    }

    @Operation(summary = "프사 업데이트", description = "새로운 프사 Url로 수정할 수 있습니다.")
    @PatchMapping("/updateProfileUrl/{userId}")
    fun updateProfileUrl(
            @PathVariable userId: Long,
            @RequestBody request: UpdateProfileUrlDto
    ) {
        mypageService.updateProfileUrl(userId, request)
    }

    @Operation(summary = "작성 글 조회", description = "작성한 글들의 ID를 반환합니다.")
    @GetMapping("/getPost/{userId}")
    fun getPostList(@PathVariable userId: Int): List<PostDto> {
        return mypageService.getPostListByUserId(userId)
    }

    @Operation(summary = "작성 댓글 조회", description = "작성한 댓글들의 ID와 게시글 ID를 반환합니다.")
    @GetMapping("/getComments/{userId}")
    fun getCommentList(@PathVariable userId: Int): List<PostCommentDto> {
        return mypageService.getPostCommentListByUserId(userId)
    }

    @Operation(summary = "찜한 글 조회", description = "찜한 글들의 ID를 반환합니다.")
    @GetMapping("/getScrap/{userId}")
    fun getPostScrapList(@PathVariable userId: Int): List<PostScrapDto> {
        return mypageService.getPostScrapListByUserId(userId)
    }

    @Operation(summary = "찜 취소", description = "특정 글의 찜을 철회홥니다.")
    @DeleteMapping("/deleteScrap/{scrapid}")
    fun deletePostScrapid(
            @PathVariable scrapid: Int,
            @RequestParam userId: Int
    ) {
        mypageService.deletePostScrap(scrapid, userId)
    }

    @Operation(summary = "신청한 글 조회", description = "신청한 글들의 ID를 반환합니다.")
    @GetMapping("/getParticipant/{userId}")
    fun getPostParticipantList(@PathVariable userId: Int): List<PostParticipantDto> {
        return mypageService.getPostParticipantListByUserId(userId)
    }

    @Operation(summary = "신청한 글 취소", description = "특정 글의 신청을 철회홥니다.")
    @DeleteMapping("/deleteParticipant/{participantid}")
    fun deletePostParticipant(
            @PathVariable postparticipantId: Int,
            @RequestParam userId: Int
    ) {
        mypageService.deletePostParticipant(postparticipantId, userId)
    }
}