package io.sprout.api.aws.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    var accessKey: String = "",
    var secretKey: String = "",
    var region: String = ""
)
