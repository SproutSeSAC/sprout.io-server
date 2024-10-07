package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
class GoogleOAuthPropertiesConfig {
    lateinit var clientId: String
    lateinit var clientSecret: String
}