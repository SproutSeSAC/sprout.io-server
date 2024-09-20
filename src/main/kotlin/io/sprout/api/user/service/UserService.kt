package io.sprout.api.user.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.config.exception.BaseException
import io.sprout.api.config.exception.ExceptionCode
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.specification.model.dto.SpecificationDto
import io.sprout.api.specification.repository.DomainRepository
import io.sprout.api.specification.repository.JobRepository
import io.sprout.api.techStack.repository.TechStackRepository
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.model.entities.*
import io.sprout.api.user.repository.UserDomainRepository
import io.sprout.api.user.repository.UserJobRepository
import io.sprout.api.user.repository.UserRepository
import io.sprout.api.user.repository.UserTechStackRepository
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
    private val jobRepository: JobRepository,
    private val domainRepository: DomainRepository,
    private val techStackRepository: TechStackRepository,
    private val userJobRepository: UserJobRepository,
    private val userDomainRepository: UserDomainRepository,
    private val userTechStackRepository: UserTechStackRepository,
    private val securityManager: SecurityManager,
    private val jwtToken: JwtToken
) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    fun checkAndJoinUser(email: String, response: HttpServletResponse) {
        val user = userRepository.findByEmail(email)
        val temporaryCourse = courseRepository.findCourseById(1) ?: throw BaseException(ExceptionCode.NOT_FOUND_COURSE)
        val newNick = NicknameGenerator.generate()
        val savedUser: UserEntity
        if (user == null) {
            // 새 유저 생성
            val newUser = UserEntity(
                email = email,
                nickname = newNick,
                role = RoleType.PRE_TRAINEE,
                status = UserStatus.INACTIVE,
                profileImageUrl = null,
                isEssential = false,
                course = temporaryCourse
            )
            savedUser = userRepository.save(newUser)
            println("New user created with ID: ${savedUser.id}")

        } else {
            // 기존 유저 처리
            savedUser = user
            println("Existing user with ID: ${savedUser.id}")
        }

        // 저장된 사용자 객체 기반으로 토큰 생성
        val refreshToken = setTokenCookiesAndReturnRefresh(savedUser, response)
        println("refreshToken: $refreshToken")

        // 유저의 refreshToken 업데이트
        savedUser.addRefreshToken(refreshToken)

        // 변경 사항 저장
        userRepository.save(savedUser)

    }

    private fun setTokenCookiesAndReturnRefresh(user: UserEntity, response: HttpServletResponse): String {
        val userId = user.id as Long
        val accessToken = jwtToken.createAccessTokenFromMemberId(userId, user.isEssential)
        val refreshToken = jwtToken.createRefreshToken(userId)
        val accessCookie = CookieUtils.createCookie("access_token", accessToken)
        val refreshCookie = CookieUtils.createCookie("refresh_token", refreshToken)
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
        return refreshToken
    }

    @Transactional
    fun createUser(request: UserDto.CreateUserRequest) {
        val userId = securityManager.getAuthenticatedUserName()
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ExceptionCode.NOT_FOUND_MEMBER)
        val course =
            courseRepository.findCourseById(request.courseId) ?: throw BaseException(ExceptionCode.NOT_FOUND_COURSE)
        val allDomainList = domainRepository.findAll()
        val allJobList = jobRepository.findAll()

        if (!user.isEssential) {
            user.course = course
            user.name = request.name
            user.nickname = request.nickname
            user.role = request.role
            user.status = UserStatus.ACTIVE
            user.marketingConsent = request.marketingConsent
            user.isEssential = true

            user.userJobList.plusAssign(
                request.jobIdList.map { jobId ->
                    UserJobEntity(
                        job = allJobList.first { it.id == jobId },
                        user = user
                    )
                }
            )

            user.userDomainList.plusAssign(
                request.domainIdList.map { domainId ->
                    UserDomainEntity(
                        domain = allDomainList.first { it.id == domainId },
                        user = user
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

    @Transactional
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

    @Transactional
    fun updateUser(request: UserDto.UpdateUserRequest) {
        val user = userRepository.findById(request.userId).orElseThrow { BaseException(ExceptionCode.NOT_FOUND_MEMBER) }

        if (user.status == UserStatus.LEAVE || user.status == UserStatus.SLEEP) {
            throw BaseException(ExceptionCode.UPDATE_FAIL)
        }

        val allDomainList = domainRepository.findAll()
        val allJobList = jobRepository.findAll()
        val allTechStackList = techStackRepository.findAll()


        user.profileImageUrl = request.profileImageUrl
        user.nickname = request.nickname.toString()

        if (request.updatedJobIdList.isNotEmpty()) {
            userJobRepository.deleteAll(user.userJobList)
            user.userJobList.clear()

            user.userJobList.plusAssign(
                request.updatedJobIdList.map { jobId ->
                    UserJobEntity(
                        job = allJobList.first { it.id == jobId },
                        user = user
                    )
                }
            )
        }

        if (request.updatedDomainIdList.isNotEmpty()) {
            userDomainRepository.deleteAll(user.userDomainList)
            user.userDomainList.clear()

            user.userDomainList.plusAssign(
                request.updatedDomainIdList.map { domainId ->
                    UserDomainEntity(
                        domain = allDomainList.first { it.id == domainId },
                        user = user
                    )
                }
            )
        }

        if (request.updatedTechStackIdList.isNotEmpty()) {
            userTechStackRepository.deleteAll(user.userTechStackList)
            user.userTechStackList.clear()

            user.userTechStackList.plusAssign(
                request.updatedTechStackIdList.map { techStackId ->
                    UserTechStackEntity(
                        techStack = allTechStackList.first { it.id == techStackId },
                        user = user
                    )
                }
            )
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            throw BaseException(ExceptionCode.UPDATE_FAIL)
        }
    }

    fun getUserInfo(userId: Long): UserDto.GetUserResponse {
        val userId2 = securityManager.getAuthenticatedUserName()!!
        val user = userRepository.findUserById(userId2) ?: throw BaseException(ExceptionCode.NOT_FOUND_MEMBER)

        return UserDto.GetUserResponse(
            name = user.name,
            nickname = user.nickname,
            profileImageUrl = user.profileImageUrl,
            jobList = user.userJobList.map {
                SpecificationDto.JobInfoDto(
                    id = it.id,
                    job = it.job.jobType.name
                )
            }.toMutableSet(),
            domainList = user.userDomainList.map {
                SpecificationDto.DomainInfoDto(
                    id = it.id,
                    domain = it.domain.domainType.name
                )
            }.toMutableSet(),
            techStackList = user.userTechStackList.map {
                SpecificationDto.TechStackInfoDto(
                    id = it.id,
                    techStack = it.techStack.name
                )
            }.toMutableSet()
        )
    }


}