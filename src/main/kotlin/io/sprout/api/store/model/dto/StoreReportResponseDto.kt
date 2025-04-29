package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.StoreReportEntity
import java.time.LocalDateTime

data class StoreReportResponseDto (
    val reportId: Long,
    val type: String,
    val targetStoreName: String?,
    val content: String,
    val createdAt: LocalDateTime,

    val store: StoreDto?,
) {
    data class StoreDto(
        val id: Long,
        val name: String,
        val address: String,
    )

    constructor(entity: StoreReportEntity) : this(
        entity.id,
        entity.type,
        entity.targetStoreName,
        entity.content,
        entity.createdAt,
        entity.store?.let { StoreDto(it.id, it.name, it.address) }
    )

}