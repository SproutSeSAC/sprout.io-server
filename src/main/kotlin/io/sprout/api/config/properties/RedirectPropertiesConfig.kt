package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "url")
class RedirectPropertiesConfig(
    var redirectUrl: String = ""
)
