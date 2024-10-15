package io.sprout.api.store.controller

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/store")
class StoreController(
    private val storeService: StoreService
) {

    @GetMapping("/{storeId}")
    @Operation(summary = "맛집 개별 조회", description = "맛집 개별 조회")
    fun getStoreDetail(@PathVariable storeId: Long): StoreDto.StoreDetailResponse {
        return storeService.getStoreDetail(storeId)
    }

    @GetMapping("/list")
    @Operation(summary = "맛집 리스트 조회", description = "맛집 리스트 조회, 필터 체크되는 경우만 true, 아닐 경우 false")
    fun getStoreList(
        @RequestParam(defaultValue = "false") isZeropay: Boolean,
        @RequestParam(defaultValue = "false") underPrice: Boolean,
        @RequestParam(defaultValue = "false") overFivePerson: Boolean,
        @RequestParam(defaultValue = "false") walkTimeWithinFiveMinutes: Boolean,
        @RequestParam foodTypeList: MutableSet<FoodType> = LinkedHashSet(),
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Map<String, Any?>> {

        val pageIndex = page - 1
        val filterRequest = StoreDto.StoreListRequest(
            isZeropay,
            underPrice,
            overFivePerson,
            walkTimeWithinFiveMinutes,
            foodTypeList,
            pageIndex,
            size
        )

        val (storeList, totalCount) = storeService.getStoreList(filterRequest)

        val totalPages = if (totalCount % size == 0L) {
            totalCount / size
        } else {
            totalCount / size + 1
        }

        val nextPage = if (page < totalPages) page + 1 else null

        val responseBody = mapOf(
            "storeList" to storeList,
            "totalCount" to totalCount,
            "currentPage" to page,  // 프론트에서 보낸 페이지 번호 반환 (1부터 시작)
            "pageSize" to size,
            "totalPages" to totalPages,  // 총 페이지 수
            "nextPage" to nextPage  // 다음 페이지 (마지막 페이지일 경우 null)
        )

        return ResponseEntity.ok(responseBody)
    }

    @GetMapping("/filterCount")
    @Operation(summary = "맛집 필터 카운트 조회", description = "맛집 필터 카운트 조회")
    fun getFilterCount(): StoreDto.StoreFilterResponse {
        return storeService.getFilterCount()
    }

}