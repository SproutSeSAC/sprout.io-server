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

                    // 쿠키를 다시 설정하여 응답에 추가
                    val newCookie = Cookie(cookie.name, cookie.value)
                    newCookie.path = "/" // 모든 경로에서 쿠키가 유효하도록 설정
                    newCookie.domain = "localhost" // 도메인을 localhost로 설정
                    newCookie.isHttpOnly = cookie.isHttpOnly
                    newCookie.secure = false // HTTP 환경에서는 false, HTTPS 환경에서는 true로 변경 가능
                    newCookie.setAttribute("SameSite", "Lax") // HTTP 환경에서 Lax 사용

                    // 응답에 새로운 쿠키 추가
                    response.addCookie(newCookie)
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