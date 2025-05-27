package io.sprout.api.user.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.domain.JwtToken
import io.sprout.api.campus.infra.CampusRepository
import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.model.entities.PageResponse
import io.sprout.api.config.exception.BaseException
import io.sprout.api.config.exception.ExceptionCode
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.service.MypageService
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.specification.repository.DomainRepository
import io.sprout.api.specification.repository.JobRepository
import io.sprout.api.specification.repository.TechStackRepository
import io.sprout.api.user.model.dto.*
import io.sprout.api.user.model.entities.*
import io.sprout.api.user.repository.*
import io.sprout.api.utils.AuthorizationUtils
import io.sprout.api.utils.CookieUtils
import io.sprout.api.utils.NicknameGenerator
import io.sprout.api.verificationCode.repository.VerificationCodeRepository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val i = 9999

@Service
class UserService(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val campusRepository: CampusRepository,
    private val jobRepository: JobRepository,
    private val domainRepository: DomainRepository,
    private val techStackRepository: TechStackRepository,
    private val userJobRepository: UserJobRepository,
    private val userDomainRepository: UserDomainRepository,
    private val userTechStackRepository: UserTechStackRepository,
    private val securityManager: SecurityManager,
    private val jwtToken: JwtToken,
    private val verificationCodeRepository: VerificationCodeRepository,
    private val jdbcTemplate: JdbcTemplate,
    private val mypageService: MypageService,
    private val userMemoRepository: UserMemoRepository,
    private val postRepository: PostRepository
) {
    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    /**
     * email 기반으로 유저 확인 후, 저장된 유저가 없다면 새로 생성
     */
    fun checkAndJoinUser(email: String, response: HttpServletResponse): UserEntity {
        val user = userRepository.findByEmail(email)
        val savedUser: UserEntity
        val nickname: String

        if (user == null) {
            // 새 유저 생성
            val newUser = UserEntity(
                email = email,
                nickname = NicknameGenerator.generate(),
                role = RoleType.PRE_TRAINEE,
                status = UserStatus.INACTIVE,
                profileImageUrl = "",
                isEssential = false
            )
            savedUser = userRepository.save(newUser)
            log.debug("New user created with ID: ${savedUser.id}")
        } else {
            // 기존 유저 처리
            savedUser = user
            log.debug("Existing user with ID: ${savedUser.id}")
        }

        // 저장된 사용자 객체 기반으로 토큰 생성
        val refreshToken = setTokenCookiesAndReturnRefresh(savedUser, response)
        log.debug("refreshToken: $refreshToken")

        // 유저의 refreshToken 업데이트
        savedUser.addRefreshToken(refreshToken)

        // 변경 사항 저장
        return userRepository.save(savedUser)

    }

    /**
     * User의 필수정보를 입력하는 과정
     * OPERATION_MANAGER & CAMPUS_LEADER 는 추가적인 CAMPUS_ID LIST 필요
     *
     * @param request User 필수정보 파라미터
     * @param accessToken Access Token String
     *
     * @return Refresh Token String
     */
    @Transactional
    fun setEssentialUserProfile(
        request: CreateUserRequest,
        accessToken: String?
    ): String {
        val userId: Long = jwtToken
            .getUserIdFromAccessToken(accessToken ?: throw CustomBadRequestException("Invalid Token"))
            .toLong()

        val userEntity = userRepository.findById(userId).orElseThrow { CustomBadRequestException("Not found user") }

        if (!userEntity.isEssential) {
            userEntity.register(
                request,
                courseRepository.findAllById(request.courseIdList),
                campusRepository.findAllById(request.campusIdList),
                jobRepository.findAllById(request.jobIdList),
                domainRepository.findAllById(request.domainIdList),
                techStackRepository.findAllById(request.techStackIdList)
            )
        } else {
            // 이미 회원 가입이 완료된 경우
            throw CustomDataIntegrityViolationException("Already registered user")
        }

        userRepository.save(userEntity)
        log.debug("createUser, userId is: {}", userEntity.id)

        return userEntity.refreshToken!!
    }

    /**
     * user 삭제 처리
     * - user pk 를 유령회원 pk로 바꾸기
     */
    @Transactional
    fun deleteUser(userId: Long) {
        val anonymousUserId = 9999L

        val findTablesSql = """
            SELECT table_name, column_name 
            FROM information_schema.key_column_usage 
            WHERE referenced_table_name = 'user' 
        """

        val deleteTable = listOf(
            "user_course", "user_campus",
            "user_tech_stack", "user_domain", "user_job",
            "notice_participant", "project_post_participant",
            "scraped_notice", "scraped_project", "scraped_store", "scrap",
            "notification",
            "user_memo"
        )

        jdbcTemplate.queryForList(findTablesSql).forEach { row ->
            val tableName = row["table_name"] as String
            val columnName = row["column_name"] as String

            if (deleteTable.contains(tableName)) {
                val deleteSql = "DELETE FROM $tableName WHERE $columnName = ?"
                jdbcTemplate.update(deleteSql, userId)
            } else {
                val updateSql = "UPDATE $tableName SET $columnName = ? WHERE $columnName = ?"
                jdbcTemplate.update(updateSql, anonymousUserId, userId)
            }
        }

        postRepository.updateClientId(userId, anonymousUserId)

        userRepository.deleteById(userId)
    }

    /**
     * 계정 업데이트
     *
     * @param request 계정 업데이트 파라미터
     */
    @Transactional
    fun updateUser(request: UpdateUserRequest) {
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

        userRepository.save(user)
        log.debug("updateUser, userId is: ${user.id}")
    }

    /**
     * user detail 조회
     */
    fun getUserInfo(userId: Long): UserDetailResponse {
        val user = userRepository.findUserById(userId) ?: throw CustomBadRequestException("Not found user")
        val campusEntities = courseRepository.findUserCampusByUserId(userId)
        return UserDetailResponse(user, campusEntities)
    }

    /**
     * 회원 닉네임이 중복인지 확인
     * @param nickname Target 닉네임
     */
    fun isNicknameDuplicate(nickname: String){
        if (userRepository.findByNickname(nickname) != null) {
            throw CustomDataIntegrityViolationException("이미 존재하는 닉네임입니다.")
        }
    }

    /**
     * 사용자 목록 조회 (관리자용)
     * 자신의 캠퍼스 인원만 검색 가능
     */
    fun searchUsers(searchRequest: UserSearchRequestDto): PageResponse<UserSearchResponseDto> {
        val adminId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val admin = userRepository.findUserById(adminId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsAdminRole(admin)

        return userRepository.search(searchRequest, admin)
    }


    /**
     * 사용자 권한 변경 (관리자용)
     */
    @Transactional
    fun changeRoleByAdmin(updateRequest: RoleUpdateRequestDto, targetUserId: Long) {
        val adminId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val admin = userRepository.findUserById(adminId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsAdminRole(admin)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(admin, updateRequest.courseIdList)
        if (updateRequest.courseIdList.isEmpty()) {
            throw CustomBadRequestException("invalid input: course ids is empty")
        }

        val targetUser = userRepository.findUserById(targetUserId) ?: throw CustomBadRequestException("Not found user")
        targetUser.role = updateRequest.role
        targetUser.userCourseList.clear()
        targetUser.userCampusList.clear()

        targetUser.userCourseList.addAll(
            updateRequest.courseIdList
                .map { UserCourseEntity(CourseEntity(it), targetUser) }
        )

        if(updateRequest.role.equals(RoleType.OPERATION_MANAGER) || updateRequest.role.equals(RoleType.CAMPUS_LEADER) || updateRequest.role.equals(RoleType.SUPER_ADMIN)) {
            targetUser.userCampusList.addAll(
                updateRequest.campusIdList
                    .map { UserCampusEntity(CampusEntity(it), targetUser) }
            )
        }

    }


    /**
     * 사용자 조회 (관리자용)
     */
    fun getUserDetailByAdmin(userId: Long): UserDetailResponse {
        val adminId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val admin = userRepository.findUserById(adminId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsAdminRole(admin)

        return getUserInfo(userId)
    }

    /**
     * 학생 목록 검색 (관지라용)
     * 자신의 캠퍼스 인원만 검색 가능
     */
    fun searchTrainees(searchRequest: UserSearchRequestDto): PageResponse<TraineeSearchResponseDto> {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        searchRequest.roles = listOf(RoleType.TRAINEE)
        return userRepository.searchTrainee(searchRequest, manager)
    }

    /**
     * 학생에 대한 메모 작성및 수정(관리자용)
     */
    @Transactional
    fun createUserMemo(createRequest: UserMemoCreateRequestDto, traineeId: Long) {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        val memo = (userMemoRepository.findByUserIdAndTargetUserId(managerId, traineeId)
            ?: UserMemo(
                manager,
                UserEntity(traineeId),
                createRequest.content
            ))

        memo.content = createRequest.content


        userMemoRepository.save(memo)
    }

    /**
     * 학생에 대한 메모 삭제 (관리자용)
     */
    fun deleteUserMemo(traineeId: Long) {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        userMemoRepository.deleteByUserIdAndTargetUserId(managerId, traineeId)
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
        val accessCookie = CookieUtils.createCookie("access_token", accessToken, false)
        val refreshCookie = CookieUtils.createCookie("refresh_token", refreshToken)
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
        return refreshToken
    }

    @Transactional
    fun changePhoneByUserId(userId: Long, phoneNumber: PhoneNumber): String {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("해당 ID의 사용자가 없습니다. id=$userId") }
        println("user.phoneNumber = ${user.phoneNumber}")

        user.phoneNumber = phoneNumber.phoneNumber
        return user.phoneNumber
    }

    fun getUserMemo(traineeId: Long): UserMemoResponseDto? {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        return userMemoRepository.findByUserIdAndTargetUserId(managerId, traineeId)
            ?.let { UserMemoResponseDto(it) }
    }

    fun getPostListByUserId(clientId: Long, pageable: Pageable, postTypes: List<String>?): Page<PostAndNickNameDto>? {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        return mypageService.getPostListByUserId(clientId, pageable, postTypes);
    }

    fun getPostCommentListByUserId(clientId: Long, pageable: Pageable, postTypes: List<PostType>?): Page<PostCommentDto>? {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        return mypageService.getPostCommentListByUserId(clientId, pageable, postTypes)
    }

    fun getPostScrapListByUserId(userId: Long, pageable: Pageable): Page<GetPostResponseDto>? {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        return mypageService.getPostScrapListByUserId(userId, pageable)
    }

    fun getPostParticipantListByUserId(userId: Long): ParticipantListResponseDto {
        val managerId: Long = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Invalid Token")
        val manager = userRepository.findUserById(managerId) ?: throw CustomBadRequestException("Not found admin")

        AuthorizationUtils.validateUserIsManagerRole(manager)

        return mypageService.getPostParticipantListByUserId(userId)
    }
}