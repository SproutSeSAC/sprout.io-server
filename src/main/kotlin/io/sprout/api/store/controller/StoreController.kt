package io.sprout.api.store.controller

import io.sprout.api.store.model.dto.DirectionResponse
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto
import io.sprout.api.store.model.dto.Trafast
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.service.MapDirectionService
import io.sprout.api.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/store")
class StoreController(
    private val storeService: StoreService,
    private val mapDirectionService: MapDirectionService
) {

    @GetMapping("/{storeId}")
    @Operation(summary = "맛집 개별 조회", description = "맛집 개별 조회")
    fun getStoreDetail(@PathVariable storeId: Long): StoreDto.StoreDetailResponse {
        return storeService.getStoreDetail(storeId)
    }

    @GetMapping("/list")
    @Operation(summary = "맛집 리스트 조회", description = "맛집 리스트 조회, 필터 체크되는 경우만 true, 아닐 경우 false")
    fun getStoreList(
        @ModelAttribute filterRequest: StoreDto.StoreListRequest
    ): ResponseEntity<Map<String, Any?>> {
        val storeList: List<StoreProjectionDto.StoreInfoDto> = storeService.getStoreList(filterRequest)

        return ResponseEntity.ok(mapOf("stores" to storeList))
    }

    @GetMapping("/filterCount")
    @Operation(summary = "맛집 필터 카운트 조회", description = "맛집 필터 카운트 조회")
    fun getFilterCount(@RequestParam campusId: Long): StoreProjectionDto.StoreFilterCount {
        return storeService.getFilterCount(campusId)
    }

    @PostMapping("/{storeId}/scrap")
    @Operation(
        summary = "맛집 스크랩/스크랩 취소 API", // 간단한 설명
        description = "맛집 스크랩 / 스크랩 취소 API. false -> 스크랩 취소 , true -> 스크랩 됨", // 상세 설명
    )
    fun toggleScrapStore(@PathVariable storeId: Long): ResponseEntity<Boolean> {
        val result = storeService.toggleScrapStore(storeId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{storeId}/review")
    @Operation(summary = "맛집 리뷰 작성", description = "특정 맛집의 리뷰 작성")
    fun createStoreReview(
        @PathVariable storeId: Long,
        @RequestBody reviewCreateRequest: StoreDto.StoreReviewRequest
    ): ResponseEntity<Void> {
        storeService.createReview(storeId, reviewCreateRequest)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/direction")
    @Operation(summary = "식당 길찾기 API", description = "해당 식당으로의 길찾기 값 조회")
    fun getStoreDirection(
        @ModelAttribute directionRequest: StoreDto.MapDirectionRequest
    ): ResponseEntity<DirectionResponse> {
        val directionResponse = mapDirectionService.findDirection(directionRequest)

        return ResponseEntity.ok(directionResponse)
    }
}