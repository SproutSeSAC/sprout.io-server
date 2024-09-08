package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cors")
data class CorsPropertiesConfig(
    var allowedOrigins: Array<String> = arrayOf()
)