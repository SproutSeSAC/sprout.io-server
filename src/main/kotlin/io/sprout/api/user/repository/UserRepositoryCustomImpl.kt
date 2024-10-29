package io.sprout.api.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.user.model.entities.QUserEntity.userEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

class UserRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): UserRepositoryCustom {


    override fun findManagerEmailSameCourse(courseId: Long): List<UserEntity> {

        val course =  CourseEntity(courseId)

        return jpaQueryFactory
            .select(userEntity)
            .from(userEntity)
            .where(
                userEntity.course.eq(course)
                    .and(
                        userEntity.role.eq(RoleType.CAMPUS_MANAGER)
                            .or(userEntity.role.eq(RoleType.JOB_COORDINATOR))
                    )
            )
            .fetch()
    }
}