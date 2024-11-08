package io.sprout.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws")
data class AWSS3Properties(
    val credentials: Credentials,
    val s3: S3,
    val region: Region
) {

    data class Credentials(
        val accessKey: String,
        val secretKey: String
    )

    data class S3(
        val bucket: String,
//        val cloudFrontDomain: String
    )

    data class Region(
        val static: String
    )

}
