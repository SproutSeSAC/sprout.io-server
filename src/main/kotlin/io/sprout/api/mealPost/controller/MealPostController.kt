package io.sprout.api.mealPost.controller

import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.model.entities.MealPostEntity
import io.sprout.api.mealPost.service.MealPostService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mealPost")
class MealPostController(
    private val mealPostService: MealPostService
) {

    @GetMapping("/list")
    @Operation(summary = "한끼팟 리스트 조회", description = "한끼팟 리스트 조회")
    fun getMealPostList(pageable: Pageable): Page<MealPostProjection> {
        return mealPostService.getMealPostList(pageable)
    }
}