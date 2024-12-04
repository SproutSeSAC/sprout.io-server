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

    /**
     * Access token과 refresh token이 존재하는지 확인
     *
     * @param accessJws Token String
     * @param refreshJws Token String
     */
    @Throws(IOException::class)
    fun isNotExistToken(accessJws: String?, refreshJws: String?, response: HttpServletResponse): Boolean {
        return accessJws == null || refreshJws == null
    }

    /**
     * Access 토큰이 유효한지 확인
     *
     * @param accessToken Token String
     */
    @Throws(IOException::class)
    fun isInvalidAccessToken(accessToken: String?, response: HttpServletResponse): Boolean {
        if (accessToken != null && tokenService.isExpiredAccessToken(accessToken)) {
            log.debug("Token InValidated")
            return true
        }
        return false
    }

    /**
     * 필수정보 입력 회원인지 확인
     *
     * @param accessJws Token String
     */
    @Throws(IOException::class)
    fun isNotEssentialUserToken(accessJws: String, response: HttpServletResponse): Boolean {
        if (!tokenService.getIsEssentialEnsFromAccessToken(accessJws)) {
            log.info("isNotEssential User 304 response")
            return true
        }
        return false
    }
}
