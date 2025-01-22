package io.sprout.api.auth.security.manager

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties

@Configuration
class OAuth2LoginConfig(private val properties: OAuth2ClientProperties) {

    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository {
        return InMemoryClientRegistrationRepository(googleClientRegistration())
    }

    private fun googleClientRegistration(): ClientRegistration {
        val googleRegistration = properties.registration["google"]
            ?: throw IllegalArgumentException("Google registration properties not found")

        return ClientRegistration.withRegistrationId("google")
            .clientId(googleRegistration.clientId)
            .clientSecret(googleRegistration.clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope(
                "openid",
                "profile",
                "email",
                "https://www.googleapis.com/auth/calendar"  // Google Calendar API 권한 추가
            )
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline")
            .tokenUri("https://www.googleapis.com/oauth2/v4/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .clientName("Google")
            .build()
    }
}
