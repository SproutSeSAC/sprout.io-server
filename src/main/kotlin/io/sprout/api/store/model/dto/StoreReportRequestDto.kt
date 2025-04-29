package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.StoreEntity
import io.sprout.api.store.model.entities.StoreReportEntity
import io.sprout.api.user.model.entities.UserEntity

data class StoreReportRequestDto(
    val type: String,
    val storeId: Long?,
    val targetStoreName: String?,
    val content: String
){
    fun toEntity(userId: Long): StoreReportEntity {
        return StoreReportEntity(
            UserEntity(userId), storeId?.let { StoreEntity(storeId) }, type, targetStoreName, content)
    }
}