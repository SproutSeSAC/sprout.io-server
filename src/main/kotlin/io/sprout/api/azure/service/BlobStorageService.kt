package io.sprout.api.azure.service

import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.sas.BlobSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URL
import java.time.OffsetDateTime

@Service
class BlobStorageService(
    @Value("\${azure.storage.connection-string}") private val connectionString: String
) {
    private val blobServiceClient = BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient()

    fun generateSasUrl(containerName: String, blobName: String, expirationMinutes: Long): URL {
        val containerClient = blobServiceClient.getBlobContainerClient(containerName)

        val blobClient = containerClient.getBlobClient(blobName)

        val permissions = BlobSasPermission().setReadPermission(true).setCreatePermission(true).setWritePermission(true)
        val expiryTime = OffsetDateTime.now().plusMinutes(expirationMinutes)

        val sasSignatureValues = BlobServiceSasSignatureValues(expiryTime, permissions)
            .setProtocol(com.azure.storage.common.sas.SasProtocol.HTTPS_ONLY)

        val sasToken = blobClient.generateSas(sasSignatureValues)

        return URL("${blobClient.blobUrl}?$sasToken")
    }

    fun deleteBlob(containerName: String, blobName: String) {
        val containerClient = blobServiceClient.getBlobContainerClient(containerName)
        val blobClient = containerClient.getBlobClient(blobName)
        blobClient.deleteIfExists()
    }
}