package io.sprout.api.user.controller

import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.service.UserService
import io.sprout.api.utils.CookieUtils
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val jwtToken: JwtToken,
) {

    @GetMapping("/check")
    @Operation(summary = "계정 조회", description = "계정 조회")
    fun getUserInfo(request: HttpServletRequest): UserDto.GetUserResponse {
        return userService.getUserInfo(request)
    }

    @PostMapping("/register")
    @Operation(summary = "계정 등록", description = "계정 등록 (회원 상태가 변경된 새로운 access-token 발급)")
    fun createUser(
        @RequestBody @Valid request: UserDto.CreateUserRequest,
        httpServletRequest: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        val refreshToken = userService.createUser(request, httpServletRequest, response)
        val newAccessToken = jwtToken.createAccessFromRefreshToken(refreshToken)
        val accessCookie = CookieUtils.createCookie("access_token", newAccessToken)
        response.addCookie(accessCookie)

        return ResponseEntity.ok("success")
    }

    @PutMapping("/leave")
    @Operation(summary = "계정 탈퇴", description = "계정 탈퇴 (상태값 변경, 실제 삭제는 30일 후 따로 진행)")
    fun deleteUser(
        response: HttpServletResponse
    ): ResponseEntity<String> {
        userService.deleteUser()
        // 계정 탈퇴 시, 두 토큰 초기화
        val accessCookie = CookieUtils.createCookie("access_token", "")
        val refreshCookie = CookieUtils.createCookie("refresh_token", "")
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)

        return ResponseEntity.ok("success")
    }

    @PutMapping("/update")
    @Operation(summary = "계정 수정", description = "계정 수정 - 도메인, 직군, 기술 스택은 업데이트 되는 내용만 보낼 것")
    fun updateUser(@RequestBody @Valid request: UserDto.UpdateUserRequest): ResponseEntity<String> {
        userService.updateUser(request)

        return ResponseEntity.ok("success")
    }

}