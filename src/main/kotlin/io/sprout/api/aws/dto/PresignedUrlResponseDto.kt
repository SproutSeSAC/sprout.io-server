package io.sprout.api.aws.dto

data class PresignedUrlResponseDto(
    val presignedUrl: String,
    val expirationMinutes: Long
)
