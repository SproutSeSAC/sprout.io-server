package io.sprout.api.user.controller

import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {
    // TODO: 회원 가입하기

    @PostMapping("")
    @Operation(summary = "유저 계정 생성", description = "유저 계정 생성")
    fun createUser(@Valid request: UserDto.CreateUserRequest) {
        return userService.createUser()
    }

    // TODO: 회원 탈퇴하기
    fun deleteUser() {
    }

}