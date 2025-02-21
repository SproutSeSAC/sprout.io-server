package io.sprout.api.auth.security.manager

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

@Component
class CustomAuthorizationRequestResolver(
    private val clientRegistrationRepository: ClientRegistrationRepository
) : OAuth2AuthorizationRequestResolver {

    // DefaultOAuth2AuthorizationRequestResolver 사용
    private val defaultAuthorizationRequestResolver = DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository, "/oauth2/authorization"
    )

    override fun resolve(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultAuthorizationRequestResolver.resolve(request)
        return customizeAuthorizationRequest(authorizationRequest)
    }

    override fun resolve(request: HttpServletRequest?, clientRegistrationId: String?): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId)
        return customizeAuthorizationRequest(authorizationRequest)
    }

    private fun customizeAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?): OAuth2AuthorizationRequest? {
        if (authorizationRequest == null) {
            return null
        }

        // 기존 파라미터만 가져오고, 추가적인 access_type 파라미터는 생략
        val additionalParams = authorizationRequest.additionalParameters.toMutableMap()

        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(additionalParams)
            .build()
    }


}
