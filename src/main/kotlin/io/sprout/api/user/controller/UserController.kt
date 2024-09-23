package io.sprout.api.user.controller

import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.service.UserService
import io.sprout.api.utils.CookieUtils
import io.swagger.v3.oas.annotations.Operation
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

    @GetMapping("")
    @Operation(summary = "계정 조회", description = "계정 조회")
    fun getUserInfo(): UserDto.GetUserResponse {
        return userService.getUserInfo()
    }

    @PostMapping("")
    @Operation(summary = "계정 등록", description = "계정 등록")
    fun createUser(@RequestBody @Valid request: UserDto.CreateUserRequest, response: HttpServletResponse): ResponseEntity<String> {
        val refreshToken = userService.createUser(request, response)
        val newAccessToken = jwtToken.createAccessFromRefreshToken(refreshToken)
        val accessCookie = CookieUtils.createCookie("access_token", newAccessToken)
        response.addCookie(accessCookie)

        return ResponseEntity.ok("success")
    }

    // TODO: 회원 탈퇴
    @PutMapping("/leave")
    @Operation(summary = "계정 탈퇴", description = "계정 탈퇴 (상태값 변경, 실제 삭제는 30일 후 따로 진행)")
    fun deleteUser(@RequestBody @Valid request: UserDto.DeleteUserRequest) {
        userService.deleteUser(request)
    }

    @PutMapping("")
    @Operation(summary = "계정 수정", description = "계정 수정")
    fun updateUser(@RequestBody @Valid request: UserDto.UpdateUserRequest) {
        userService.updateUser(request)
    }

}