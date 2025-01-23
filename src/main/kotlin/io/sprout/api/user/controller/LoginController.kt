package io.sprout.api.user.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.utils.CookieUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(
    private val jwtToken: JwtToken,
    private val securityManager: SecurityManager
) {

    /**
     *  access 와  refresh 검증을 위한 토큰
     */
    @GetMapping("/check")
    fun loginCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }

    /**
     *  Login 성공시 200번 응답
     */
    @GetMapping("/success")
    fun success(request: HttpServletRequest, response: HttpServletResponse) {
        val log = LoggerFactory.getLogger(this::class.java)

        // 쿠키에서 access_token과 refresh_token 값을 추출
        var accessToken: String? = null
        var refreshToken: String? = null

        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                when (cookie.name) {
                    "access_token" -> accessToken = cookie.value
                    "refresh_token" -> refreshToken = cookie.value
                }
            }
        } else {
            log.info("No cookies found in the request.")
        }

        // 리디렉션할 URL에 쿼리 파라미터로 access_token과 refresh_token 추가
        val redirectUrl = buildString {
            append("https://prod-sprout.duckdns.org/login-check")
            if (accessToken != null && refreshToken != null) {
                append("?access_token=$accessToken&refresh_token=$refreshToken")
            }
        }

        log.info("Redirecting to: {}", redirectUrl)

        // 리디렉션 처리
        response.sendRedirect(redirectUrl)
    }

    /**
     *  Login 실패시 401
     */
    @GetMapping("/failure")
    fun failure(): ResponseEntity<String> {
        return ResponseEntity.status(401).body("fail")
    }

    /**
     *  refresh 로 access 재생성
     */
    @GetMapping("/refresh")
    fun refresh(
        @RequestHeader("Refresh-Token") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<Map<String, String>> {
        val newAccessToken = jwtToken.createAccessFromRefreshToken(refreshToken)
        val accessCookie = CookieUtils.createCookie("access_token", newAccessToken)
        response.addCookie(accessCookie)
        val map = hashMapOf("access_token" to newAccessToken)
        return ResponseEntity.ok(map)
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }
}
