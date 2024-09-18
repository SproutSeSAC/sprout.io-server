package io.sprout.api.user.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.user.service.UserService
import io.sprout.api.utils.CookieUtils
import jakarta.servlet.http.Cookie
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
     *  Login 성공시 200번 응답
     */
    @GetMapping("/success")
    fun success(request: HttpServletRequest, response: HttpServletResponse) {
        val log = LoggerFactory.getLogger(this::class.java)

        // 쿠키 값을 가져옴
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "access_token" || cookie.name == "refresh_token") {
                    log.info("Cookie Name: {}, Value: {}", cookie.name, cookie.value)

                    // 쿠키 값을 헤더에 추가
                    response.addHeader("Set-Cookie", "${cookie.name}=${cookie.value}; Path=/; HttpOnly")
                }
            }
        } else {
            log.info("No cookies found in the request.")
        }

        // 리디렉션
        response.sendRedirect("http://localhost:3000")
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
    fun refresh(@RequestHeader("refresh_token") refreshToken: String, response: HttpServletResponse): ResponseEntity<String> {
        val newAccessToken=jwtToken.createAccessFromRefreshToken(refreshToken)
        val accessCookie = CookieUtils.createCookie("access_token", newAccessToken)
        response.addCookie(accessCookie)
        return ResponseEntity.ok("success")
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        println("securty :{${securityManager.getAuthenticatedUserName()}}")
        return ResponseEntity.ok("success")
    }
}