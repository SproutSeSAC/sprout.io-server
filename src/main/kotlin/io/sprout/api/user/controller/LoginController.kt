package io.sprout.api.user.controller

import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.user.service.UserService
import io.sprout.api.utils.CookieUtils
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(
    private val jwtToken: JwtToken
) {
    /**
     *  Login 성공시 200번 응답
     */
    @GetMapping("/success")
    fun success(): ResponseEntity<String> {
        return ResponseEntity.ok("success")
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
    fun refresh(@RequestHeader("refreshJws") refreshToken: String, response: HttpServletResponse): ResponseEntity<String> {
        val newAccessToken=jwtToken.createAccessFromRefreshToken(refreshToken)
        val accessCookie = CookieUtils.createCookie("access_token", newAccessToken)
        response.addCookie(accessCookie)
        return ResponseEntity.ok("success")
    }
}