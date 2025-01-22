package io.sprout.api.auth.security.handler

import io.sprout.api.config.properties.RedirectPropertiesConfig
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class CustomAuthenticationFailureHandler(
    @Qualifier("redirectPropertiesConfig")  // 특정 빈 지정
    private val redirectPropertiesConfig: RedirectPropertiesConfig
) : AuthenticationFailureHandler {

    private val log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        log.error("{}", exception)
        log.error("Authentication failed: {}", exception.message)

        // 실패 후 리다이렉트할 URL 설정
        response.sendRedirect("http://localhost:8080/api/login/failure")
    }
}