package io.sprout.api.store.service

import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreDto.StoreDetailResponse.StoreMenuDetail
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
            StoreDto.StoreListResponse.StoreDetail(
                id = store.id,
                name = store.name,
                storeImage = store.storeImageList.firstOrNull()?.path,
                workingDay = store.workingDay,
                breakTime = store.breakTime,
                walkTimeWithinFiveMinutes = store.walkTime <= 5,
                overFivePerson = store.isOverPerson,
                // TODO: 쿼리 결과에는 어짜피 제외되서 오기 때문에, 일단 request로 처리
                underPrice = filterRequest.underPrice
            )
        }
        val totalCount = storeList.second

        return Pair(result, totalCount)
    }

    fun getFilterCount(): StoreDto.StoreFilterResponse {

        val storeList = storeRepository.findStoreFilterList()

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
            snackCount = storeList.count { it.foodType == FoodType.SNACK }
        )
    }

    fun getStoreDetail(storeId: Long): StoreDto.StoreDetailResponse {
        val store = storeRepository.findById(storeId).orElseThrow { throw CustomBadRequestException("Not found store") }

        return StoreDto.StoreDetailResponse(
            name = store.name,
            storeImage = store.storeImageList.firstOrNull()?.path,
            breakTime = store.breakTime,
            workingDay = store.workingDay,
            phoneNumber = store.name,
            walkTimeWithinFiveMinutes = store.walkTime <= 5,
            overFivePerson = store.isOverPerson,
            underPrice = store.storeMenuList.any { it.price <= 10000 },
            storeMenuList = store.storeMenuList.map {
                StoreMenuDetail(
                    id = it.id,
                    name = it.name,
                    price = it.price,
                    imageUrl = it.imageUrl
                )
            }.sortedBy { it.id }.toMutableSet()
        )

    }
}