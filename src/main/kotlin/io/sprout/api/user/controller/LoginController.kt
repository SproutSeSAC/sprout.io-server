package io.sprout.api.user.controller

import io.sprout.api.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(
    private val userService: UserService
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
}