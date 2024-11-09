package io.sprout.api.user.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.config.exception.BaseException
import io.sprout.api.config.exception.ExceptionCode
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.specification.repository.DomainRepository
import io.sprout.api.specification.repository.JobRepository
import io.sprout.api.specification.repository.TechStackRepository
import io.sprout.api.user.model.dto.UserDto
import io.sprout.api.user.model.entities.*
import io.sprout.api.user.repository.UserDomainRepository
import io.sprout.api.user.repository.UserJobRepository
import io.sprout.api.user.repository.UserRepository
import io.sprout.api.user.repository.UserTechStackRepository
import io.sprout.api.utils.CookieUtils
import io.sprout.api.utils.NicknameGenerator
import io.sprout.api.verificationCode.repository.VerificationCodeRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
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
    private val jwtToken: JwtToken,
    private val verificationCodeRepository: VerificationCodeRepository
) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    fun checkAndJoinUser(email: String, response: HttpServletResponse): UserEntity {
        val user = userRepository.findByEmail(email)
        val newNick = NicknameGenerator.generate()
        val savedUser: UserEntity
        if (user == null) {
            // 새 유저 생성
            val newUser = UserEntity(
                email = email,
                nickname = newNick,
                role = RoleType.PRE_TRAINEE,
                status = UserStatus.INACTIVE,
                profileImageUrl = "https://aaa.com",
                isEssential = false
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
        return userRepository.save(savedUser)

    }

    @Transactional
    fun createUser(
        request: UserDto.CreateUserRequest,
        httpServletRequest: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        try {
            val userId = getUserIdFromAccessToken(httpServletRequest)
            val userEntity = userRepository.findById(userId).orElseThrow { CustomBadRequestException("Not found user") }
            val userCourseList: MutableSet<UserCourseEntity> = mutableSetOf()
            courseRepository.findAllById(request.courseIdList).map { courseEntity ->
                userCourseList.plusAssign(
                    UserCourseEntity(
                        course = courseEntity,
                        user = userEntity
                    )
                )
            }

            val allDomainList = domainRepository.findAll()
            val allJobList = jobRepository.findAll()
            val allTechStackList = techStackRepository.findAll()

            if (!userEntity.isEssential) {
                userEntity.userCourseList = userCourseList
                userEntity.name = request.name
                userEntity.nickname = request.nickname
                userEntity.role = request.role
                userEntity.status = UserStatus.ACTIVE
                userEntity.marketingConsent = request.marketingConsent
                userEntity.isEssential = true

                userEntity.userJobList.plusAssign(
                    request.jobIdList.map { jobId ->
                        UserJobEntity(
                            job = allJobList.first { it.id == jobId },
                            user = userEntity
                        )
                    }
                )

                userEntity.userDomainList.plusAssign(
                    request.domainIdList.map { domainId ->
                        UserDomainEntity(
                            domain = allDomainList.first { it.id == domainId },
                            user = userEntity
                        )
                    }
                )

                userEntity.userTechStackList.plusAssign(
                    request.techStackIdList.map { techStackId ->
                        UserTechStackEntity(
                            techStack = allTechStackList.first { it.id == techStackId },
                            user = userEntity
                        )
                    }
                )

            } else {
                // 이미 회원 가입이 완료된 경우
                throw CustomDataIntegrityViolationException("Already registered user")
            }

            userRepository.save(userEntity)
            log.debug("createUser, userId is: {}", userEntity.id)

            return userEntity.refreshToken!!

        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("User data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while saving the user: ${e.message}")
        } catch (e: Exception) {
            // Bad Request
            throw CustomBadRequestException("Bad Request: ${e.message}")
        }
    }

    @Transactional
    fun deleteUser() {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Check reissued access token")
        val user = userRepository.findById(userId).orElseThrow { CustomBadRequestException("Not found user") }
        user.status = UserStatus.LEAVE
        user.refreshToken = ""

        try {
            userRepository.save(user)
            log.debug("deleteUser, userId is: ${user.id}")
        } catch (e: JpaSystemException) {
            throw CustomSystemException("System error occurred while deleting user: ${e.message}")
        }
    }

    @Transactional
    fun updateUser(request: UserDto.UpdateUserRequest) {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Check reissued access token")
        val user = userRepository.findById(userId).orElseThrow { CustomBadRequestException("Not found user") }

        if (user.status == UserStatus.LEAVE || user.status == UserStatus.SLEEP) {
            throw BaseException(ExceptionCode.UPDATE_FAIL)
        }

        val allDomainList = domainRepository.findAll()
        val allJobList = jobRepository.findAll()
        val allTechStackList = techStackRepository.findAll()


        if (request.profileImageUrl != null) {
            user.profileImageUrl = request.profileImageUrl
        }

        if (request.nickname != null) {
            user.nickname = request.nickname
        }

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
            log.debug("updateUser, userId is: ${user.id}")
        } catch (e: JpaSystemException) {
            throw CustomSystemException("System error occurred while updating user: ${e.message}")
        }
    }

    fun getUserInfo(request: HttpServletRequest): UserDto.GetUserResponse {
        try {
            val userId = getUserIdFromAccessToken(request)
            val user = userRepository.findUserById(userId) ?: throw CustomBadRequestException("Not found user")

            return UserDto.GetUserResponse(
                name = user.name,
                email = user.email,
                campusList = user.userCampusList.map {
                    it.campus.name
                }.toMutableSet(),
                courseList = user.userCourseList.map {
                    UserDto.GetUserResponse.CourseDetail(
                        courseTitle = it.course.title,
                        courseStartDate = it.course.startDate,
                        courseEndDate = it.course.endDate
                    )
                }.toMutableSet(),
                nickname = user.nickname,
                role = user.role,
                profileImageUrl = user.profileImageUrl,
                jobList = user.userJobList.map {
                    SpecificationsDto.JobInfoDto(
                        id = it.job.id,
                        job = it.job.name
                    )
                }.toMutableSet(),
                domainList = user.userDomainList.map {
                    SpecificationsDto.DomainInfoDto(
                        id = it.domain.id,
                        domain = it.domain.name
                    )
                }.toMutableSet(),
                techStackList = user.userTechStackList.map {
                    SpecificationsDto.TechStackInfoDto(
                        id = it.techStack.id,
                        techStack = it.techStack.name,
                        iconImageUrl = it.techStack.path ?: "",
                        jobName = it.techStack.jobName ?: ""
                    )
                }.toMutableSet()
            )
        } catch (e: JpaSystemException) {
            throw CustomSystemException("System error occurred while saving the project: ${e.message}")
        } catch (e: Exception) {
            // Bad Request
            throw CustomBadRequestException("Authorization error: ${e.message}")
        }
    }

    fun verifyCode(code: String) {
        val codeList = verificationCodeRepository.findAll()
        val existentCode = codeList.firstOrNull { it.code == code }

        if (existentCode != null) {
            existentCode.useCount += 1
            try {
                verificationCodeRepository.save(existentCode)
            } catch (e: Exception) {
                throw CustomSystemException("System error occurred while saving code-use count: ${e.message}")
            }
        } else {
            throw CustomBadRequestException("Invalid verification code")
        }

    }

    private fun setTokenCookiesAndReturnRefresh(user: UserEntity, response: HttpServletResponse): String {
        val userId = user.id
        val accessToken = jwtToken.createAccessTokenFromMemberId(userId, user.isEssential)
        val refreshToken = jwtToken.createRefreshToken(userId)
        val accessCookie = CookieUtils.createCookie("access_token", accessToken)
        val refreshCookie = CookieUtils.createCookie("refresh_token", refreshToken)
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
        return refreshToken
    }

    private fun getUserIdFromAccessToken(request: HttpServletRequest): Long {
        val accessJws: String? = request.getHeader("Access-Token")
        val userId = jwtToken.getUserIdFromAccessToken(accessJws!!)
        return userId.toLong()
    }

}