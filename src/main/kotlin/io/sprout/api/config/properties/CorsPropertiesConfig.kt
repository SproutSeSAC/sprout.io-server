package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsPropertiesConfig(
    var allowedOrigins: Array<String> = arrayOf()
)
