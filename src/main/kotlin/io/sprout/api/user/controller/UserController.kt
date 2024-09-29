package io.sprout.api.user.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.service.GoogleUserService
import io.sprout.api.user.service.UserService
import io.sprout.api.utils.CookieUtils
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.view.RedirectView
import java.net.URI

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val jwtToken: JwtToken,
    private val googleUserService: GoogleUserService,
    private val restTemplate: RestTemplate,
    private val securityManager: SecurityManager
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
    fun deleteUser(@RequestBody @Valid request: UserDto.DeleteUserRequest, response: HttpServletResponse): ResponseEntity<String> {
        userService.deleteUser(request)
        // 계정 탈퇴 시, 두 토큰 초기화
        val accessCookie = CookieUtils.createCookie("access_token", "")
        val refreshCookie = CookieUtils.createCookie("refresh_token", "")
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)

        return ResponseEntity.ok("success")
    }

    @PutMapping("")
    @Operation(summary = "계정 수정", description = "계정 수정 - 도메인, 직군, 기술 스택은 업데이트 되는 내용만 보낼 것")
    fun updateUser(@RequestBody @Valid request: UserDto.UpdateUserRequest): ResponseEntity<String> {
        userService.updateUser(request)

        return ResponseEntity.ok("success")
    }


    @GetMapping("/calendar")
    fun redirectToGoogleCalendar(response: HttpServletResponse): ResponseEntity<Any> {
        println("여기2")
        val userId = securityManager.getAuthenticatedUserName()
        val user = UserEntity(userId!!)

        // Access Token을 가져오고 만료 확인 후 갱신
        val googleToken = googleUserService.refreshAccessToken(user)
            ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token is expired. Please reauthenticate.")

        // Google Calendar API 호출
        val url = "https://www.googleapis.com/calendar/v3/calendars/primary/events"
        val headers = HttpHeaders()
        headers.setBearerAuth(googleToken.accessToken)  // 갱신된 Access Token을 Authorization 헤더에 설정

        val entity = HttpEntity<Void>(headers)
        val calendarResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)

        if (!calendarResponse.statusCode.is2xxSuccessful) {
            return ResponseEntity.status(calendarResponse.statusCode).body("Failed to fetch calendar events")
        }

        // JSON 응답에서 첫 번째 이벤트의 htmlLink 추출
        val htmlLink = extractHtmlLink(calendarResponse.body ?: throw IllegalStateException("No events found"))

        // Google Calendar 이벤트로 리다이렉트
        val redirectView = RedirectView()
        redirectView.url = htmlLink
        return ResponseEntity.status(HttpStatus.FOUND).location(URI(htmlLink)).build()
    }

    // JSON 파싱 메서드 (Jackson이나 Gson을 사용할 수도 있습니다)
    fun extractHtmlLink(jsonResponse: String): String {
        // 정규식을 사용하여 첫 번째 이벤트의 htmlLink를 추출
        val htmlLinkPattern = "\"htmlLink\":\\s*\"(.*?)\"".toRegex()
        return htmlLinkPattern.find(jsonResponse)?.groups?.get(1)?.value
            ?: throw IllegalStateException("No htmlLink found in response")
    }
}