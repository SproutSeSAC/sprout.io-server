package io.sprout.api.store.service

import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreDto.StoreDetailResponse.*
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.repository.StoreRepository
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository
) {
    fun getStoreList(filterRequest: StoreDto.StoreListRequest): Pair<List<StoreDto.StoreListResponse.StoreDetail>, Long> {

        val storeList = storeRepository.findStoreList(filterRequest)

        val result = storeList.first.map { store ->

            val tagList = mutableListOf<String>().apply {
                if (store.walkTime <= 5) add("도보 5분 이내")
                if (store.isOverPerson) add("5인 이상")
                if (store.storeMenuList.any { it.price!! <= 10000 }) add("만원이하")
                if (store.isZeropay) add("제로페이")
            }

            StoreDto.StoreListResponse.StoreDetail(
                id = store.id,
                name = store.name,
                foodType = store.foodType,
                campusName = store.campusName,
                mapSchemaUrl = store.mapSchemaUrl,
                storeImage = store.storeImageList.firstOrNull()?.path,
                workingDay = store.workingDay,
                walkTime = store.walkTime,
                tagList = tagList,
                address = store.address,
                contact = store.contact,
                breakTime = store.breakTime
            )
        }
        val totalCount = storeList.second

        return Pair(result, totalCount)
    }

    fun getFilterCount(campusId: Long): StoreDto.StoreFilterResponse {

        val storeList = storeRepository.findStoreFilterList(campusId)

        return StoreDto.StoreFilterResponse(
            zeropayCount = storeList.count { it.isZeropay },
            underPriceCount = storeList.map { it.storeMenuList.filter { menu -> menu.price!! <= 10000L } }.count(),
            overPersonCount = storeList.count { it.isOverPerson },
            walkTimeCount = storeList.count { it.walkTime <= 5 },
            koreanFoodCount = storeList.count { it.foodType == FoodType.KOREAN },
            chineseFoodCount = storeList.count { it.foodType == FoodType.CHINESE },
            japanesesFoodCount = storeList.count { it.foodType == FoodType.JAPANESE },
            westernFoodCount = storeList.count { it.foodType == FoodType.WESTERN },
            asianFoodCount = storeList.count { it.foodType == FoodType.ASIAN },
            snackCount = storeList.count { it.foodType == FoodType.SNACK },
            cafeCount = storeList.count { it.foodType == FoodType.CAFE }
        )
    }

    fun getStoreDetail(storeId: Long): StoreDto.StoreDetailResponse {

        val store = storeRepository.findStoreById(storeId) ?: throw CustomBadRequestException("Not found store")
        val tagList = mutableListOf<String>().apply {
            if (store.walkTime <= 5) add("도보 5분 이내")
            if (store.isOverPerson) add("5인 이상")
            if (store.storeMenuList.any { it.price <= 10000 }) add("만원이하")
            if (store.isZeropay) add("제로페이")
        }

        return StoreDto.StoreDetailResponse(
            name = store.name,
            storeImageList = store.storeImageList.map { it.path },
            address = store.address,
            campusName = store.campus.name,
            breakTime = store.breakTime,
            workingDay = store.workingDay,
            contact = store.contact,
            tagList = tagList,
            foodType = store.foodType,
            walkTime = store.walkTime,
            storeMenuList = store.storeMenuList.map {
                StoreMenuDetail(
                    id = it.id,
                    name = it.name,
                    price = it.price,
                    imageUrl = it.imageUrl
                )
            }.sortedBy { it.id }.toMutableSet(),
            storeReviewList = store.storeReviewList.map {
                StoreReviewDetail(
                    nickname = it.user.nickname,
                    review = it.review,
                    profileImageUrl = it.user.profileImageUrl,
                    rating = it.rating,
                    createdAt = it.createdAt
                )
            }.sortedByDescending { it.createdAt }.toMutableSet(),
        )

    }
}