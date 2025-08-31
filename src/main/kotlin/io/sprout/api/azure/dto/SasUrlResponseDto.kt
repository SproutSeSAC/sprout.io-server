package io.sprout.api.azure.dto

data class SasUrlResponseDto(
    val sasUrl: String,
    val expirationMinutes: Long
)