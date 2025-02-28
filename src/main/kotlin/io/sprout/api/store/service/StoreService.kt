package io.sprout.api.store.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.scrap.repository.ScrapRepository
import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreDto.StoreDetailResponse.*
import io.sprout.api.store.model.dto.StoreProjectionDto
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.model.entities.ScrapedStoreEntity
import io.sprout.api.store.model.entities.StoreEntity
import io.sprout.api.store.model.entities.StoreReviewEntity
import io.sprout.api.store.repository.ScrapedStoreRepository
import io.sprout.api.store.repository.StoreRepository
import io.sprout.api.store.repository.StoreReviewRepository
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val scrapedStoreRepository: ScrapedStoreRepository,
    private val securityManager: SecurityManager,
    private val storeReviewRepository: StoreReviewRepository,
    private val postRepository: PostRepository,
    private val scrapRepository: ScrapRepository
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
        var tmp = storeRepository.findStoreList(
            filterRequest,
            securityManager.getAuthenticatedUserName()!!
        )

        tmp.forEach { storeInfoDto ->
            val post = postRepository.findByLinkedIdAndPostType(storeInfoDto.id, PostType.STORE)
                ?: throw CustomBadRequestException("게시글(Post)이 없습니다.")

            storeInfoDto.postId = post.id
            storeInfoDto.isScraped = scrapRepository.findByUserIdAndPostId(
                securityManager.getAuthenticatedUserId(), post.id
            ) != null
        }

        return tmp
    }

    fun getFilterCount(campusId: Long): StoreProjectionDto.StoreFilterCount {

        return storeRepository.findStoreFilterList(campusId)
    }

    fun getStoreDetail(storeId: Long): StoreDto.StoreDetailResponse {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")
        val store = storeRepository.findStoreById(storeId) ?: throw CustomBadRequestException("Not found store")

        val post = postRepository.findByLinkedIdAndPostType(store.id, PostType.STORE)
            ?: throw CustomBadRequestException("게시글(Post)이 없습니다.")

        val isScraped = ((scrapRepository.findByUserIdAndPostId(userId, post.id)) != null)
//        val isScraped = scrapedStoreRepository.findByUserIdAndStoreId(userId, storeId) != null

        return StoreDto.StoreDetailResponse(
            name = store.name,
            storeImageList = store.storeImageList.map { it.path },
            address = store.address,
            campusName = store.campus.name,
            breakTime = store.breakTime,
            workingDay = store.workingDay,
            contact = store.contact,
            foodType = store.foodType,
            walkTime = store.walkTime,
            isZeropay = store.isZeropay,
            isOverPerson = store.isOverPerson,
            isScraped = isScraped,
            postId = post.id,
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

    fun createReview(storeId: Long, reviewCreateRequest: StoreDto.StoreReviewRequest) {
        val store = storeRepository.findStoreById(storeId) ?: throw CustomBadRequestException("Not found store")

        storeReviewRepository.save(StoreReviewEntity(
            reviewCreateRequest.review,
            reviewCreateRequest.rating,
            UserEntity(securityManager.getAuthenticatedUserName()!!),
            store))

    }
}