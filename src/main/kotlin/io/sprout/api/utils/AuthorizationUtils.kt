package io.sprout.api.utils

import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

object AuthorizationUtils {
    /**
     * SUPER_ADMIN, CAMPUS_LEADER, OPERATION_MANAGER, EDU_MANAGER, JOB_COORDINATOR, INSTRUCTOR
     */
    fun validateUserIsManagerRole(user: UserEntity) {
        val isManagerRole = listOf(
            RoleType.SUPER_ADMIN,
            RoleType.CAMPUS_LEADER,
            RoleType.OPERATION_MANAGER,
            RoleType.EDU_MANAGER,
            RoleType.JOB_COORDINATOR,
            RoleType.INSTRUCTOR
        )
            .contains(user.role)

        if (isManagerRole.not()) {
            throw CustomBadRequestException("user role ${user.role} is not authorization")
        }
    }

    /**
     * SUPER_ADMIN, CAMPUS_LEADER, OPERATION_MANAGER
     */
    fun validateUserIsAdminRole(user: UserEntity) {
        val isManagerRole = listOf(
            RoleType.SUPER_ADMIN,
            RoleType.CAMPUS_LEADER,
            RoleType.OPERATION_MANAGER
        )
            .contains(user.role)

        if (isManagerRole.not()) {
            throw CustomBadRequestException("user role ${user.role} is not authorization")
        }
    }

    /**
     * targetCourse 모두 가지고 있는지
     */
    fun validateUserCourseContainAllTargetCourses(user: UserEntity, targetCourses: Set<Long>) {
        val isUserCourseContainsAllTargetCourse = user.userCourseList
            .map { uc -> uc.course.id }
            .containsAll(targetCourses)

        if (isUserCourseContainsAllTargetCourse.not()) {
            throw CustomBadRequestException("user courses are not contain all target courses")
        }
    }

    /**
     * 적어도 targetCourse중 하나의 course를 가지고 있는지
     */
    fun validateUserCourseContainAtLeastOneTargetCourses(user: UserEntity, targetCourses: Set<Long>) {
        val isNotUserCourseContainsAtLeastOneTargetCourse = user.userCourseList
            .map { uc -> uc.course.id }
            .none { targetCourses.contains(it) }

        if (isNotUserCourseContainsAtLeastOneTargetCourse) {
            throw CustomBadRequestException("user courses are not contain target course")
        }
    }

}