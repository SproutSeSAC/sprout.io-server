package io.sprout.api.aws.dto

data class DeleteFileRequestDto(
    val bucketName: String,
    val objectKey: String
)