package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtPropertiesConfig(
    var accessToken: TokenProperties = TokenProperties(),
    var refreshToken: TokenProperties = TokenProperties()
) {
    data class TokenProperties(
        var secret: String = "",
        var expiration: Long = 0
    )
}