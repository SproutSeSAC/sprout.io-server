package io.sprout.api.auth.token.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.token.domain.JwtToken
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.io.IOException

@Service
class TokenValidatorService(private val tokenService: JwtToken) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    @Throws(IOException::class)
    fun isNotExistToken(accessJws: String?, refreshJws: String?, response: HttpServletResponse): Boolean {
        if (accessJws == null || refreshJws == null) {
            response.status = HttpStatus.NOT_FOUND.value()
            return true
        }
        return false
    }

    @Throws(IOException::class)
    fun isInvalidAccessToken(accessToken: String?, response: HttpServletResponse): Boolean {
        println("ㅇㅇㅇ")
        if (accessToken != null && tokenService.isExpiredAccessToken(accessToken)) {
            log.debug("Token InValidated")
            response.status = HttpStatus.UNAUTHORIZED.value()
            return true
        }
        return false
    }

    @Throws(IOException::class)
    fun isNotEssentialUserToken(accessJws: String, response: HttpServletResponse): Boolean {
        if (!tokenService.getIsEssentialEnsFromAccessToken(accessJws)) {
            log.info("isNotEssential User 304 response")
            response.status = HttpStatus.NOT_MODIFIED.value()
            return true
        }
        return false
    }
}
