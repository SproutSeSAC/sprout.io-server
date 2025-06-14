package io.sprout.api.test

import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.user.repository.UserRepository
import io.sprout.api.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class OriginTestController(
    private val jwtToken: JwtToken,
    private val testService: OriginTestService,
    private val userService: UserService,
    private val userRepository: UserRepository
) {
    @Value("\${user.email}")
    lateinit var email: String

    @GetMapping("/")
    fun getTestResponse(): String {
        return testService.test()
    }

    @GetMapping("/getAdminCookie")
    @Operation(summary = "Application 계정 단편 로그인")
    fun getTokens(response: HttpServletResponse): Map<String, String?> {
        val user = userService.checkAndJoinUser(email, response)

        val accessToken = user.refreshToken?.let { refreshToken ->
            jwtToken.createAccessFromRefreshToken(refreshToken)
        }

        user.refreshToken?.let { refreshToken ->
            val refreshCookie = Cookie("refresh_token", refreshToken).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(refreshCookie)
        }

        accessToken?.let { token ->
            val accessCookie = Cookie("access_token", token).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(accessCookie)
        }

        return mapOf(
            "refresh_token" to user.refreshToken,
            "access_token" to accessToken
        )
    }

    @GetMapping("/getAdminCookie2")
    @Operation(summary = "Application 계정 단편 로그인2")
    fun getTokens2(response: HttpServletResponse): Map<String, String?> {
        val user = userService.checkAndJoinUser("cong8685@naver.com", response)

        val accessToken = user.refreshToken?.let { refreshToken ->
            jwtToken.createAccessFromRefreshToken(refreshToken)
        }

        user.refreshToken?.let { refreshToken ->
            val refreshCookie = Cookie("refresh_token", refreshToken).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(refreshCookie)
        }

        accessToken?.let { token ->
            val accessCookie = Cookie("access_token", token).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(accessCookie)
        }

        return mapOf(
            "refresh_token" to user.refreshToken,
            "access_token" to accessToken
        )
    }

    @GetMapping("/getAdminCookie3")
    @Operation(summary = "Application 계정 단편 로그인2")
    fun getTokens3(response: HttpServletResponse): Map<String, String?> {
        val user = userService.checkAndJoinUser("test@naver.com", response)

        val accessToken = user.refreshToken?.let { refreshToken ->
            jwtToken.createAccessFromRefreshToken(refreshToken)
        }

        user.refreshToken?.let { refreshToken ->
            val refreshCookie = Cookie("refresh_token", refreshToken).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(refreshCookie)
        }

        accessToken?.let { token ->
            val accessCookie = Cookie("access_token", token).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(accessCookie)
        }

        return mapOf(
            "refresh_token" to user.refreshToken,
            "access_token" to accessToken
        )
    }

    @GetMapping("/getUserCookie")
    @Operation(summary = "Application 계정 단편 로그인")
    fun getUserTokens(
        @RequestParam email: String,
        response: HttpServletResponse
    ): Map<String, String?> {
        userRepository.findByEmail(email) ?: throw CustomBadRequestException("not exist email")

        val user = userService.checkAndJoinUser(email, response)

        val accessToken = user.refreshToken?.let { refreshToken ->
            jwtToken.createAccessFromRefreshToken(refreshToken)
        }

        user.refreshToken?.let { refreshToken ->
            val refreshCookie = Cookie("refresh_token", refreshToken).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(refreshCookie)
        }

        accessToken?.let { token ->
            val accessCookie = Cookie("access_token", token).apply {
                path = "/"
                isHttpOnly = false
                secure = false
                setAttribute("SameSite", "None")
            }
            response.addCookie(accessCookie)
        }

        return mapOf(
            "refresh_token" to user.refreshToken,
            "access_token" to accessToken
        )
    }
}