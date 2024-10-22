package io.sprout.api.store.repository

import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto

interface StoreRepositoryCustom {

    fun findStoreList(request: StoreDto.StoreListRequest): Pair<List<StoreProjectionDto.StoreInfoDto>, Long>

    fun findStoreFilterList(campusId: Long): List<StoreProjectionDto.StoreFilterDto>
}
