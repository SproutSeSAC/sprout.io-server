package io.sprout.api.user.service

import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.user.infra.UserRepository
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.model.entities.UserStatus
import io.sprout.api.utils.CookieUtils
import io.sprout.api.utils.NicknameGenerator
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val jwtToken: JwtToken
) {
    fun checkAndJoinUser(email: String, response: HttpServletResponse) {
        val user = userRepository.findByEmail(email)
        val temporaryCourse = courseRepository.findCourseById(1)
        val newNick = NicknameGenerator.generate()
        if (user == null) {
            // 사용자 생성
            val newUser = UserEntity(
                email = email,
                nickname = newNick,
                role = RoleType.User,
                status = UserStatus.INACTIVE,
                profileImageUrl = null,
                isEssential = false,
                course = temporaryCourse
            )
            val savedUser = userRepository.save(newUser)
            val userId = savedUser.id as Long
            val accessToken = jwtToken.createAccessTokenFromMemberId(userId, savedUser.isEssential)
            val refreshToken = jwtToken.createRefreshToken(userId);
            val accessCookie = CookieUtils.createCookie("access_token", accessToken)
            val refreshCookie = CookieUtils.createCookie("refresh_token", refreshToken)
            response.addCookie(accessCookie)
            response.addCookie(refreshCookie)
        } else {
            // 기존 사용자 처리
            val userId = user.id
            val accessToken = jwtToken.createAccessTokenFromMemberId(userId, user.isEssential)
            val refreshToken = jwtToken.createRefreshToken(userId);
            val accessCookie = CookieUtils.createCookie("access_token", accessToken)
            val refreshCookie = CookieUtils.createCookie("refresh_token", refreshToken)
            response.addCookie(accessCookie)
            response.addCookie(refreshCookie)
        }

    }

    fun createUser() {

    }
}