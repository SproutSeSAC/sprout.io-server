package io.sprout.api.aws.service

import io.sprout.api.aws.config.AwsProperties
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration

@Service
class S3Service(private val awsProperties: AwsProperties) {

    private val s3Client: S3Client = S3Client.builder()
        .region(Region.of(awsProperties.region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    awsProperties.accessKey,
                    awsProperties.secretKey
                )
            )
        )
        .build()

    private val s3Presigner: S3Presigner = S3Presigner.builder()
        .region(Region.of(awsProperties.region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    awsProperties.accessKey,
                    awsProperties.secretKey
                )
            )
        )
        .build()

    fun generatePresignedUrl(bucketName: String, objectKey: String, contentType: String, expirationMinutes: Long): URL {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .contentType(contentType)
            .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .build()

        return s3Presigner.presignPutObject(presignRequest).url()
    }

    fun deleteFile(bucketName: String, objectKey: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }
}
