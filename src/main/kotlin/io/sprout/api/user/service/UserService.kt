package io.sprout.api.user.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.config.exception.BaseException
import io.sprout.api.config.exception.ExceptionCode
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.techStack.repository.TechStackRepository
import io.sprout.api.user.infra.UserDomainRepository
import io.sprout.api.user.infra.UserJobRepository
import io.sprout.api.user.infra.UserRepository
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.model.entities.*
import io.sprout.api.utils.CookieUtils
import io.sprout.api.utils.NicknameGenerator
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val userJobRepository: UserJobRepository,
    private val userDomainRepository: UserDomainRepository,
    private val techStackRepository: TechStackRepository,
    private val jwtToken: JwtToken
) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    fun checkAndJoinUser(email: String, response: HttpServletResponse) {
        val user = userRepository.findByEmail(email)
        val temporaryCourse = courseRepository.findCourseById(99) ?: throw BaseException(ExceptionCode.NOT_FOUND_COURSE)
        val newNick = NicknameGenerator.generate()
        if (user == null) {
            // 사용자 생성
            val newUser = UserEntity(
                email = email,
                nickname = newNick,
                role = RoleType.PRE_TRAINEE,
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

    @Transactional
    fun createUser(request: UserDto.CreateUserRequest) {
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ExceptionCode.NOT_FOUND_MEMBER)
        val course =
            courseRepository.findCourseById(request.courseId) ?: throw BaseException(ExceptionCode.NOT_FOUND_COURSE)

        if (!user.isEssential) {
            user.course = course
            user.name = request.name
            user.nickname = request.nickname
            user.role = request.role
            user.status = UserStatus.ACTIVE
            user.marketingConsent = request.marketingConsent
            user.isEssential = true

            user.userJobList.plusAssign(
                request.jobList.map {
                    UserJobEntity(
                        user = user,
                        jobType = it
                    )
                }
            )

            user.userDomainList.plusAssign(
                request.domainList.map {
                    UserDomainEntity(
                        user = user,
                        domainType = it
                    )
                }
            )

        } else {
            // 이미 회원 가입이 완료된 경우
            throw BaseException(ExceptionCode.ALREADY_REGISTERED_USER)
        }

        try {
            userRepository.save(user)
            log.debug("createUser, userId is: ${user.id}")
        } catch (e: Exception) {
            throw BaseException(ExceptionCode.CREATE_FAIL)
        }
    }

    fun deleteUser(request: UserDto.DeleteUserRequest) {
        val user = userRepository.findById(request.userId).orElseThrow { BaseException(ExceptionCode.NOT_FOUND_MEMBER) }
        user.status = UserStatus.LEAVE

        try {
            userRepository.save(user)
            log.debug("deleteUser, userId is: ${user.id}")
        } catch (e: Exception) {
            throw BaseException(ExceptionCode.DELETE_FAIL)
        }
    }

    fun updateUser(request: UserDto.UpdateUserRequest) {
        val user = userRepository.findById(request.userId).orElseThrow { BaseException(ExceptionCode.NOT_FOUND_MEMBER) }

        if (user.status == UserStatus.LEAVE || user.status == UserStatus.SLEEP) {
            throw BaseException(ExceptionCode.UPDATE_FAIL)
        }

        val techStackList = techStackRepository.findAll()

        user.profileImageUrl = request.profileImageUrl
        user.nickname = request.nickname

        if (request.updatedJobList.isNotEmpty()) {
            val deleteUserJobList = user.userJobList
            user.userJobList.removeAll(deleteUserJobList)
            userJobRepository.deleteAll(deleteUserJobList)

            user.userJobList.plusAssign(
                request.updatedJobList.map {
                    UserJobEntity(
                        user = user,
                        jobType = it
                    )
                }
            )
        }

        if (request.updatedDomainList.isNotEmpty()) {
            val deleteUserDomainList = user.userDomainList
            user.userDomainList.removeAll(deleteUserDomainList)
            userDomainRepository.deleteAll(deleteUserDomainList)

            user.userJobList.plusAssign(
                request.updatedJobList.map {
                    UserJobEntity(
                        user = user,
                        jobType = it
                    )
                }
            )
        }

//        if (request.updatedTechStackList.isNotEmpty()) {
//
//        }



    }


}