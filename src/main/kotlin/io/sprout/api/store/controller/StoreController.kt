package io.sprout.api.store.controller

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/store")
class StoreController(
    private val storeService: StoreService
) {

    @GetMapping("/list")
    @Operation(summary = "맛집 리스트 조회", description = "맛집 리스트 조회")
    fun getStoreList(
        @RequestParam isZeropay: Boolean?,
        @RequestParam underPrice: Boolean?,
        @RequestParam overFivePerson: Boolean?,
        @RequestParam walkTimeWithinFiveMinutes: Boolean?,
        @RequestParam foodTypeList: List<FoodType>,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): StoreDto.GetStoreListResponse {

        val pageIndex = page - 1
        val filterRequest = StoreDto.GetStoreListRequest(
            isZeropay,
            underPrice,
            overFivePerson,
            walkTimeWithinFiveMinutes,
            foodTypeList,
            pageIndex,
            size
        )

        return storeService.getStoreList(filterRequest)
    }

}