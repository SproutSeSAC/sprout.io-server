package io.sprout.api.store.repository

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto

interface StoreRepositoryCustom {

    fun findStoreList(request: StoreDto.StoreListRequest, userId: Long): List<StoreProjectionDto.StoreInfoDto>

    fun findStoreFilterList(campusId: Long): StoreProjectionDto.StoreFilterCount
}
