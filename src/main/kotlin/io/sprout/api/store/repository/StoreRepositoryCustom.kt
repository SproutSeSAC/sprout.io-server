package io.sprout.api.store.repository

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto

interface StoreRepositoryCustom {

    fun findStoreList(request: StoreDto.GetStoreListRequest): List<StoreProjectionDto.StoreInfoDto>
}
