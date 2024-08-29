package io.sprout.api.domain.auth

import io.sprout.api.config.security.iwt.JwtDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/google")
    fun loginGoogle(@RequestBody @Valid request: AuthDto.AuthenticationGoogleRequest): JwtDto.Response {
        return authService.loginGoogle(request)
    }
}