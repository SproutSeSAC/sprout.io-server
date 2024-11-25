package io.sprout.api.aws.controller

import io.sprout.api.aws.dto.PresignedUrlRequestDto
import io.sprout.api.aws.dto.PresignedUrlResponseDto
import io.sprout.api.aws.service.S3Service
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import java.net.URL

@RestController
@RequestMapping("/aws")
class S3Controller(private val s3Service: S3Service) {

    @PostMapping("/uploadurl")
    @Operation(summary = "Presigned URL 발행", description = "업로드를 위한 URL을 발행합니다.")
    fun getPresignedUrl(@RequestBody request: PresignedUrlRequestDto): PresignedUrlResponseDto {
        val presignedUrl: URL = s3Service.generatePresignedUrl(
            bucketName = request.bucketName,
            objectKey = request.objectKey,
            contentType = request.contentType,
            expirationMinutes = request.expirationMinutes
        )

        return PresignedUrlResponseDto(
            presignedUrl = presignedUrl.toString(),
            expirationMinutes = request.expirationMinutes
        )
    }
}
