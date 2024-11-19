package io.sprout.api.mypage.controller

import io.sprout.api.mypage.dto.CardDto
import io.sprout.api.mypage.service.MypageService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mypage")
class MypageController(private val mypageService: MypageService) {

    @Operation(summary = "프로필 조회", description = "프로필 카드 조회 API, 프로필과 교육과정 카드에 필요한 정보를 반환")
    @GetMapping("/getcard")
    fun getUserCard(@PathVariable userId: Long): CardDto.UserCard {
        return mypageService.getUserCard(userId)
    }
}