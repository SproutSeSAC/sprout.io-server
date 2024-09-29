package io.sprout.api.auth.security.handler

import io.sprout.api.config.properties.RedirectPropertiesConfig
import io.sprout.api.user.service.GoogleUserService
import io.sprout.api.user.service.UserService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class CustomAuthenticationSuccessHandler(
    private val userService: UserService,
    private val redirectPropertiesConfig: RedirectPropertiesConfig,
    private val authorizedClientService: OAuth2AuthorizedClientService, // 의존성 추가
    private val googleUserService: GoogleUserService
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // OAuth2AuthenticationToken으로 캐스팅
        val oauth2AuthenticationToken = authentication as OAuth2AuthenticationToken
        val oauth2User: OAuth2User = oauth2AuthenticationToken.principal
        val clientRegistrationId = oauth2AuthenticationToken.authorizedClientRegistrationId

        // 사용자 속성 가져오기
        val attributes: Map<String, Any> = oauth2User.attributes
        val email = attributes["email"] as String

        log.info("User email: {}", email)

        // OAuth2AuthorizedClientService를 사용하여 OAuth2AuthorizedClient 가져오기
        val authorizedClient: OAuth2AuthorizedClient? = authorizedClientService.loadAuthorizedClient(
            clientRegistrationId,
            oauth2AuthenticationToken.name
        )

        // 액세스 토큰과 리프레시 토큰 가져오기
        val accessToken: OAuth2AccessToken? = authorizedClient?.accessToken
        val refreshToken = authorizedClient?.refreshToken

        log.info("Access Token: {}", accessToken?.tokenValue)
        log.info("Refresh Token: {}", refreshToken?.tokenValue)

        // 사용자 서비스 로직 실행
        val user = userService.checkAndJoinUser(email, response)

        // 액세스 토큰 만료 시간 계산
        val expiresIn: Int? = accessToken?.expiresAt?.let { expiresAt ->
            val currentTime = java.time.Instant.now()
            java.time.Duration.between(currentTime, expiresAt).seconds.toInt()
        }

        log.info("Expires In: {} seconds", expiresIn)

        // GoogleToken 저장
        if (accessToken != null && expiresIn != null) {
            googleUserService.saveOrUpdateToken(user, accessToken.tokenValue, refreshToken?.tokenValue, expiresIn)
        }

        // 인증 성공 후 리다이렉트
        response.sendRedirect(redirectPropertiesConfig.redirectUrl)
    }
}
