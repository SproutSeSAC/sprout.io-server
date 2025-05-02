package io.sprout.api.user.controller

import io.sprout.api.utils.CookieUtils
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sproutLogout")
class LogoutController {

    @GetMapping
    fun logoutHandle(response: HttpServletResponse): ResponseEntity<String> {
        CookieUtils.addCookie(response, "refresh_token", "", 0)
        return ResponseEntity.ok("로그아웃 완료되었습니다.")
    }
}
