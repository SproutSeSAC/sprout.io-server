package io.sprout.api.auth.security.handler

import io.sprout.api.config.properties.RedirectPropertiesConfig
import io.sprout.api.user.service.UserService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class CustomAuthenticationSuccessHandler(
    private val userService: UserService,
    private val redirectPropertiesConfig: RedirectPropertiesConfig
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = authentication.principal as OAuth2User
        val attributes: Map<String, Any> = user.attributes
        val email = attributes["email"] as String

    }
}