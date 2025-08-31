package io.sprout.api.azure.controller

import io.sprout.api.azure.dto.DeleteBlobRequestDto
import io.sprout.api.azure.dto.SasUrlRequestDto
import io.sprout.api.azure.dto.SasUrlResponseDto
import io.sprout.api.azure.service.BlobStorageService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URL

@RestController
@RequestMapping("/azure")
class BlobStorageController(private val blobStorageService: BlobStorageService) {

    @PostMapping("/uploadurl")
    @Operation(summary = "SAS URL 발행", description = "업로드를 위한 URL을 발행합니다.")
    fun getSasUrl(@RequestBody request: SasUrlRequestDto): SasUrlResponseDto {
        val sasUrl: URL = blobStorageService.generateSasUrl(
            containerName = request.containerName,
            blobName = request.blobName,
            expirationMinutes = request.expirationMinutes
        )

        return SasUrlResponseDto(
            sasUrl = sasUrl.toString(),
            expirationMinutes = request.expirationMinutes
        )
    }

    @DeleteMapping("/deletefile")
    @Operation(summary = "파일 삭제", description = "삭제 할 이미지 파일을 선택합니다.")
    fun deleteFile(@RequestBody request: DeleteBlobRequestDto): ResponseEntity<String> {
        return try {
            blobStorageService.deleteBlob(request.containerName, request.blobName)
            ResponseEntity.ok("파일 삭제 성공")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("파일 삭제 실패 : ${e.message}")
        }
    }
}