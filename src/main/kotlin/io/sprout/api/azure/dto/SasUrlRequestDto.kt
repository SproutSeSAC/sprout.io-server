package io.sprout.api.azure.dto

data class SasUrlRequestDto(
    val containerName: String,
    val blobName: String,
    val expirationMinutes: Long = 1
)