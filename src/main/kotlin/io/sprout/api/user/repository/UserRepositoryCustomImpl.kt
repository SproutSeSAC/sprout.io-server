package io.sprout.api.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.user.model.entities.QUserCourseEntity
import io.sprout.api.user.model.entities.QUserEntity.userEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

class UserRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): UserRepositoryCustom {


    override fun findManagerEmailSameCourse(courseId: Long): List<UserEntity> {

        val userCourseEntity = QUserCourseEntity("userCourseEntity")

        return jpaQueryFactory
            .select(userEntity)
            .from(userEntity)
            .leftJoin(userEntity.userCourseList, QUserCourseEntity.userCourseEntity)
            .where(
                userCourseEntity.course.id.eq(courseId)
                    .and(
                        userEntity.role.eq(RoleType.CAMPUS_MANAGER)
                            .or(userEntity.role.eq(RoleType.JOB_COORDINATOR))
                    )
            )
            .fetch()
    }
}