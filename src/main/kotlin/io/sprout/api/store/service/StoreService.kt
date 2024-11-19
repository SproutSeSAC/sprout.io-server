package io.sprout.api.store.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreDto.StoreDetailResponse.*
import io.sprout.api.store.model.dto.StoreProjectionDto
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.model.entities.ScrapedStoreEntity
import io.sprout.api.store.repository.ScrapedStoreRepository
import io.sprout.api.store.repository.StoreRepository
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val scrapedStoreRepository: ScrapedStoreRepository,
    private val securityManager: SecurityManager
) {

    private fun <T> handleExceptions(action: () -> T): T {
        return try {
            action()
        } catch (e: DataIntegrityViolationException) {
            throw CustomDataIntegrityViolationException("Data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            throw CustomSystemException("System error occurred: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw CustomUnexpectedException("Invalid input: ${e.message}")
        } catch (e: Exception) {
            throw CustomUnexpectedException("Unexpected error occurred: ${e.message}")
        }
    }

    fun getStoreList(filterRequest: StoreDto.StoreListRequest): List<StoreProjectionDto.StoreInfoDto> {
        return storeRepository.findStoreList(
            filterRequest,
            securityManager.getAuthenticatedUserName()!!)
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

    fun toggleScrapStore(storeId: Long): Boolean {
        return handleExceptions {
            val user = UserEntity(securityManager.getAuthenticatedUserName()!!)
            val store = storeRepository.findById(storeId).orElseThrow { IllegalArgumentException("Store not found") }
            val existingScrap = scrapedStoreRepository.findByUserAndStore(user, store)

            if (existingScrap != null) {
                scrapedStoreRepository.delete(existingScrap)
                false
            } else {
                scrapedStoreRepository.save(
                    ScrapedStoreEntity(
                        user = user,
                        store = store
                    )
                )
                true
            }
        }
    }
}