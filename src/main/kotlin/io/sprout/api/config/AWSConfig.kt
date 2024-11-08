package io.sprout.api.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AWSConfig(
    private val awsS3Properties: AWSS3Properties
) {

    @Bean
    fun amazonS3Client(): AmazonS3 {
        val awsCredentials: AWSCredentials = BasicAWSCredentials(awsS3Properties.credentials.accessKey, awsS3Properties.credentials.secretKey)
        return AmazonS3ClientBuilder.standard()
            .withRegion(awsS3Properties.region.static)
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .build()
    }

}