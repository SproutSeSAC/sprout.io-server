package io.sprout.api.common.model.entities

data class PageResponse<T>(
    val content: List<T> = listOf(),
    val totalCount: Long,
)