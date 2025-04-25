package io.sprout.api.user.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.user.model.dto.*
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.service.GoogleUserService
import io.sprout.api.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val jwtToken: JwtToken,
    private val googleUserService: GoogleUserService,
    private val securityManager: SecurityManager
) {

    /**
     * 계정 조회
     */
    @GetMapping("/check")
    @Operation(summary = "계정 조회", description = "계정 조회")
    fun getUserInfo(): UserDetailResponse {
        val userId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")

        return userService.getUserInfo(userId)
    }

    /**
     * 코드 조회
     */
    @GetMapping("/verification/{code}")
    @Operation(summary = "코드 조회", description = "코드 조회")
    fun verifyCode(@PathVariable("code") code: String) {
        return userService.verifyCode(code)
    }


    /**
     * 가입 초기 계정의 필수 추가 정보 입력 과정
     *
     *  @param UserDto.CreateUserRequest 계정 초기화 요청 파라미터
     * @return 200 with Access Token(body)
     */
    @PostMapping("/register")
    @Operation(summary = "계정 등록", description = "계정 등록 (회원 상태가 변경된 새로운 access-token 발급)")
    fun initUser(
        @RequestBody @Valid request: CreateUserRequest,
        @RequestHeader("Access-Token") accessToken: String?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Map<String,String>> {
        var realToken = accessToken
        if (accessToken == null) {
            for (cookie in httpRequest.cookies) {
                if (cookie.name == "access_token") {
                    realToken = cookie.value
                }
            }
        }

        val refreshToken = userService.setEssentialUserProfile(request, realToken)
        val newAccessToken = jwtToken.createAccessFromRefreshToken(refreshToken)

        val result = hashMapOf("access_token" to newAccessToken)

        return ResponseEntity.ok(result)
    }

    /**
     * 계정 탈퇴
     */
    @DeleteMapping("/leave")
    @Operation(summary = "계정 탈퇴", description = "계정 탈퇴 (상태값 변경, 실제 삭제는 30일 후 따로 진행)")
    fun deleteUser(
    ): ResponseEntity<String> {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Check reissued access token")

        userService.deleteUser(userId)
        return ResponseEntity.ok("success")
    }

    /**
     * 계정 업데이트
     * @param request 업데이트 요청 파라미터
     */
    @PutMapping("/update")
    @Operation(summary = "계정 수정", description = "계정 수정 - 도메인, 직군, 기술 스택은 업데이트 되는 내용만 보낼 것")
    fun updateUser(@RequestBody @Valid request: UpdateUserRequest): ResponseEntity<String> {
        userService.updateUser(request)

        return ResponseEntity.ok("success")
    }

    /**
     * 닉네임이 중복인지 확인
     */
    @GetMapping("/nickname/duplicate")
    fun getNicknameDuplicate(@RequestParam nickname: String): ResponseEntity<Any> {
        userService.isNicknameDuplicate(nickname)

        return ResponseEntity.ok().build()
    }


    @PostMapping("/calendar/{courseId}")
    fun registerGoogleCalendarId(@RequestBody calendarIdRequestDto: CalendarIdRequestDto, @PathVariable courseId: Long): ResponseEntity<String> {
        return if (googleUserService.registerGoogleCalendarId(calendarIdRequestDto.calendarId, courseId)) {
            ResponseEntity.ok("Calendar ID successfully registered")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to register Calendar ID")
        }
    }

    @GetMapping("/calendar/{courseId}")
    fun getCalendarIdWithManagerGroup(@PathVariable courseId: Long): ResponseEntity<List<CalendarIdResponseDto>> {
        val result = googleUserService.getCalendarInfoByCourseId(courseId)
        return ResponseEntity.ok(result)

    }

    @GetMapping("/calendar/{courseId}/email")
    fun getManagersByCourseId(@PathVariable courseId: Long): List<ManagerEmailResponseDto> {
        return googleUserService.findManagerEmailSameCourse(courseId)
    }

    @GetMapping("/calendar")
    fun redirectToGoogleCalendar(response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val userId = securityManager.getAuthenticatedUserName()
        val user = UserEntity(userId!!)

        // Access Token을 가져오고 만료 확인 후 갱신
        val googleToken = googleUserService.refreshAccessToken(user)
            ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
        val accessToken: Map<String, String> = mapOf("access_token" to googleToken.accessToken)
        return ResponseEntity(accessToken, HttpStatus.OK)

//        // Google Calendar API 호출
//        val url = "https://www.googleapis.com/calendar/v3/calendars/primary/events"
//        val headers = HttpHeaders()
//        headers.setBearerAuth(googleToken.accessToken)  // 갱신된 Access Token을 Authorization 헤더에 설정
//
//        val entity = HttpEntity<Void>(headers)
//        val calendarResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)
//
//        if (!calendarResponse.statusCode.is2xxSuccessful) {
//            return ResponseEntity.status(calendarResponse.statusCode).body("Failed to fetch calendar events")
//        }
//
//        // JSON 응답에서 첫 번째 이벤트의 htmlLink 추출
//        val htmlLink = extractHtmlLink(calendarResponse.body ?: throw IllegalStateException("No events found"))
//
//        // Google Calendar 이벤트로 리다이렉트
//        val redirectView = RedirectView()
//        redirectView.url = htmlLink
//        return ResponseEntity.status(HttpStatus.FOUND).location(URI(htmlLink)).build()
    }

    // JSON 파싱 메서드 (Jackson이나 Gson을 사용할 수도 있습니다)
    fun extractHtmlLink(jsonResponse: String): String {
        // 정규식을 사용하여 첫 번째 이벤트의 htmlLink를 추출
        val htmlLinkPattern = "\"htmlLink\":\\s*\"(.*?)\"".toRegex()
        return htmlLinkPattern.find(jsonResponse)?.groups?.get(1)?.value
            ?: throw IllegalStateException("No htmlLink found in response")
    }
}