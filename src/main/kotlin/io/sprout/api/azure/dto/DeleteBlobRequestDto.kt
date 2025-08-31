package io.sprout.api.azure.dto

data class DeleteBlobRequestDto(
    val containerName: String,
    val blobName: String
)