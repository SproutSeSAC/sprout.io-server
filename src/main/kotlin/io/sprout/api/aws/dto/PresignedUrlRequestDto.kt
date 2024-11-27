package io.sprout.api.aws.dto

data class PresignedUrlRequestDto(
    val bucketName: String,
    val objectKey: String,
    val contentType: String,
    val expirationMinutes: Long = 1
)
