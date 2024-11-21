package io.sprout.api.mealPost.controller

import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.service.MealPostService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mealPost")
class MealPostController(
    private val mealPostService: MealPostService
) {

    // 생성
    @PostMapping
    @Operation(summary = "한끼팟 생성", description = "한끼팟 생성 API")
    fun createMealPost(@RequestBody @Valid request: MealPostDto.MealPostCreateRequest) {
        return mealPostService.createMealPost(request)
    }

    // 지우기
    @DeleteMapping("/{mealPostId}")
    @Operation(summary = "한끼팟 삭제", description = "한끼팟 삭제 API")
    fun deleteMealPost(@PathVariable mealPostId: Long) {
        return mealPostService.deleteMealPost(mealPostId)
    }

    @GetMapping
    @Operation(summary = "한끼팟 리스트 조회", description = "한끼팟 리스트 조회 API")
    fun getMealPostList(pageable: Pageable): ResponseEntity<MealPostDto.MealPostListResponse> {
        val mealPostList = mealPostService.getMealPostList(pageable)

        return ResponseEntity.ok(MealPostDto.MealPostListResponse(mealPostList))
    }

    @GetMapping("/{mealPostId}")
    @Operation(summary = "한끼팟 상세 조회", description = "한끼팟 상세 조회 API")
    fun getMealPostDetail(@PathVariable mealPostId: Long): MealPostDto.MealPostDetailResponse {
        return mealPostService.getMealPostDetail(mealPostId)
    }

    @PutMapping("/participation")
    @Operation(summary = "한끼팟 참여", description = "한끼팟 참여 API")
    fun joinParty(@RequestBody @Valid request: MealPostDto.ParticipationRequest) {
        return mealPostService.joinParty(request)
    }

    @PutMapping("/leave")
    @Operation(summary = "한끼팟 탈퇴", description = "한끼팟 탈퇴 API")
    fun leaveParty(@RequestBody @Valid request: MealPostDto.LeaveRequest) {
        return mealPostService.leaveParty(request)
    }


}