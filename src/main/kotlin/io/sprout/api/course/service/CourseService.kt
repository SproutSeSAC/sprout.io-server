package io.sprout.api.course.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.course.model.dto.CourseRequestDto
import io.sprout.api.course.model.dto.CourseDto
import io.sprout.api.course.model.dto.CourseSearchRequestDto
import io.sprout.api.course.model.dto.CourseSearchResponseDto
import io.sprout.api.mypage.repository.UserCampusRepository
import io.sprout.api.mypage.repository.UserCourseRepository
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserCourseEntity
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val userCourseRepository: UserCourseRepository,
    private val userCampusRepository: UserCampusRepository,
    private val userRepository: UserRepository,
    private val securityManager: SecurityManager,
) {

    fun getCourseListByCampusId(campusId: Long): CourseDto.CourseListResponse {
        val courseList = courseRepository.findByCampusId(campusId).sortedBy { it.id }
        val response = courseList.map { course ->
            CourseDto.CourseListResponse.CourseDetail(
                id = course.id,
                title = course.title,
                campusName = course.campus!!.name
            )
        }

        return CourseDto.CourseListResponse(
            courseList = response
        )
    }

    /**
     * 관리자용
     * 교육과정 검색
     */
    fun searchCourse(searchRequest: CourseSearchRequestDto): CourseSearchResponseDto {
        // 관리자 권한 확인
        val user = getUser()
        validateUserIsManagerRole(user)

        return courseRepository.searchCourse(searchRequest, user)
    }

    /**
     * 관리자용
     * 교육과정 추가
     * 해당 campaus의 CAMPUS_MANAGER와 ADMIN에 교육과정 추가
     */
    fun createCourse(createRequest: CourseRequestDto) {
        val user = getUser()
        validateUserIsUpCampusManagerRole(user)
        val savedCourse = courseRepository.save(createRequest.toEntity())

        val campusManagers = userCampusRepository.findByCampusId(createRequest.campusId)
        userCourseRepository.saveAll(
            campusManagers.map { UserCourseEntity(savedCourse, it.user) }
        )

        val admins = userRepository.findByRole(RoleType.ADMIN)
        userCourseRepository.saveAll(
            admins.map { UserCourseEntity(savedCourse, it) }
        )
    }

    /**
     * 관리자용
     * 교육과정 수정
     * 교육과정의 campus가 수정된다면 CAMPUS_MANAGER의 교육과정을 수정
     */
    @Transactional
    fun updateCourse(updateRequest: CourseRequestDto, courseId: Long) {
        val user = getUser()
        validateUserIsManagerRole(user)

        val course = courseRepository.findById(courseId)
            .orElseThrow {throw CustomBadRequestException("not found course")}

        if (updateRequest.campusId != course.campus?.id) {


            val prevManagers = userCampusRepository.findByCampusId(course.campus?.id ?: 0L)
            prevManagers.forEach {
                userCourseRepository.deleteByCourseIdAndUserId(courseId, it.user.id)
            }

            val campusManagers = userCampusRepository.findByCampusId(updateRequest.campusId)
            userCourseRepository.saveAll(
                campusManagers.map { UserCourseEntity(course, it.user) }
            )
        }

        course.update(updateRequest)
    }




    private fun getUserId(): Long {
        return securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")
    }

    private fun getUser(): UserEntity {
        return userRepository.findById(getUserId()).orElseThrow { throw CustomBadRequestException("Not found user") }
    }

    private fun validateUserIsManagerRole(user: UserEntity) {
        val isManagerRole = listOf(
            RoleType.ADMIN,
            RoleType.CAMPUS_MANAGER,
            RoleType.EDU_MANAGER,
            RoleType.JOB_COORDINATOR
        )
            .contains(user.role)

        if (isManagerRole.not()) {
            throw CustomBadRequestException("user role ${user.role} is not authorization")
        }
    }

    private fun validateUserIsUpCampusManagerRole(user: UserEntity) {
        val isManagerRole = listOf(
            RoleType.ADMIN,
            RoleType.CAMPUS_MANAGER
        )
            .contains(user.role)

        if (isManagerRole.not()) {
            throw CustomBadRequestException("user role ${user.role} is not authorization")
        }
    }
}