package io.sprout.api.store.service

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.repository.StoreRepository
import org.springframework.stereotype.Service

@Service
class StoreService(
    private val storeRepository: StoreRepository

) {
    fun getStoreList(filterRequest: StoreDto.StoreListRequest): StoreDto.StoreListResponse {
        val storeList = storeRepository.findStoreList(filterRequest)

        val result = storeList.map { store ->
            // 여기에서
            StoreDto.StoreListResponse.StoreDetail(
                name = store.name,
                storeImage = store.storeImageList.first().path ?: "",
                workingDay = store.workingDay,
                breakTime = store.breakTime
            )
        }

        return StoreDto.StoreListResponse(
            storeList = result
        )
    }
}