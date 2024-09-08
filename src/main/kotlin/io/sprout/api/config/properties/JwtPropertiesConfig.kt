package io.sprout.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtPropertiesConfig(
    val accessToken: TokenProperties = TokenProperties(),
    val refreshToken: TokenProperties = TokenProperties()
) {
    data class TokenProperties(
        var secret: String = "",
        var expiration: Long = 0
    )
}