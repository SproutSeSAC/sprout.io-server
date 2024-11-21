package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "naver.map")
class NaverMapPropertiesConfig(
    var directionUrl: String = "",
    var apiKeyId: String = "",
    var apiKey: String = ""
)
