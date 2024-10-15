package io.sprout.api.mealPost.controller

import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.service.MealPostService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mealPost")
class MealPostController(
    private val mealPostService: MealPostService
) {

    // 생성
    @PostMapping
    @Operation(summary = "한끼팟 생성", description = "한끼팟 생성 API")
    fun createMealPost(@RequestBody @Valid request: MealPostDto.CreateMealPostRequest) {
        return mealPostService.createMealPost(request)
    }

    // 지우기
    @DeleteMapping
    @Operation(summary = "한끼팟 삭제", description = "한끼팟 삭제 API")
    fun deleteMealPost(@RequestBody @Valid request: MealPostDto.DeleteMealPostRequest) {
        return mealPostService.deleteMealPost(request)
    }

    // TODO: 주최자 닉네임과 사진 뿌리기
    @GetMapping("/list")
    @Operation(summary = "한끼팟 리스트 조회", description = "한끼팟 리스트 조회 API")
    fun getMealPostList(pageable: Pageable): Page<MealPostProjection> {
        return mealPostService.getMealPostList(pageable)
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