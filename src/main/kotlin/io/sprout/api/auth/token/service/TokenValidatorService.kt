package io.sprout.api.auth.token.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.token.domain.JwtToken
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.io.IOException

@Service
class TokenValidatorService(private val tokenService: JwtToken) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    /**
     * Access token이 존재하는지 확인
     *
     * @param accessJws Token String
     */
    @Throws(IOException::class)
    fun isNotExistToken(accessJws: String?, response: HttpServletResponse): Boolean {
        return accessJws == null
    }

    /**
     * Access 토큰이 유효한지 확인
     *
     * @param accessToken Token String
     */
    @Throws(IOException::class)
    fun isInvalidAccessToken(accessToken: String?, response: HttpServletResponse): Boolean {
        if (accessToken != null && tokenService.isInvalidAccessToken(accessToken)) {
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
