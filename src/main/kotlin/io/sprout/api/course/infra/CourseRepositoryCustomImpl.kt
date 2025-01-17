package io.sprout.api.course.infra

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.campus.model.entities.QCampusEntity
import io.sprout.api.course.model.dto.CourseSearchRequestDto
import io.sprout.api.course.model.dto.CourseSearchResponseDto
import io.sprout.api.course.model.entities.QCourseEntity
import io.sprout.api.user.model.entities.QUserCourseEntity
import io.sprout.api.user.model.entities.QUserEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

class CourseRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : CourseRepositoryCustom {
    val course = QCourseEntity.courseEntity
    val campus = QCampusEntity.campusEntity
    val user = QUserEntity.userEntity
    val userCourse = QUserCourseEntity.userCourseEntity

    override fun searchCourse(searchRequest: CourseSearchRequestDto, requestUser: UserEntity): CourseSearchResponseDto {
        val myCourseIds: List<Long> = queryFactory
            .select(userCourse.course.id)
            .from(userCourse)
            .where(userCourse.user.id.eq(requestUser.id))
            .fetch()

        val courseIds = queryFactory
            .select(course.id)
            .from(course)
            .leftJoin(course.campus, campus)
            .where(
                isInCourse(myCourseIds),
                containKeyword(searchRequest.keyword),
                isInCampus(searchRequest.campusId)
            )
            .orderBy(OrderSpecifier(Order.DESC, course.startDate))
            .limit(searchRequest.size.toLong())
            .offset(searchRequest.offset.toLong())
            .fetch()

        val result = queryFactory
            .selectFrom(course)
            .leftJoin(course.campus, campus)
            .leftJoin(course.userCourseList, userCourse)
            .leftJoin(userCourse.user, user)
            .where(
                course.id.`in`(courseIds)
            )
            .transform(
                GroupBy.groupBy(course.id).list(
                    Projections.constructor(
                        CourseSearchResponseDto.CourseListViewDto::class.java,
                        course.id,
                        campus.name,
                        course.title,
                        course.startDate,
                        course.endDate,
                    )
                )
            )

        val eduManagers = queryFactory
            .selectFrom(course)
            .leftJoin(course.userCourseList, userCourse)
            .leftJoin(userCourse.user, user)
            .where(
                course.id.`in`(courseIds)
                    .and(user.role.eq(RoleType.EDU_MANAGER))
            ).transform(
                GroupBy.groupBy(course.id).list(
                    Projections.constructor(
                        CourseSearchResponseDto.EduManagerProjection::class.java,
                        course.id,
                        list(
                            Projections.constructor(
                                CourseSearchResponseDto.CampusManagerDto::class.java,
                                user.id,
                                user.name,
                                user.role
                            )
                        )

                    )
                )
            )

        val managerMap: MutableMap<Long, MutableList<CourseSearchResponseDto.CampusManagerDto>> = mutableMapOf()
        eduManagers.forEach {
            managerMap.put(it.courseId, it.eduManagers)
        }

        result.forEach { it.courseManager = managerMap.getOrDefault(it.courseId, mutableListOf()) }


        return CourseSearchResponseDto(result)
    }

    private fun isInCourse(myCoursesIds: List<Long>): BooleanExpression? {
        return course.id.`in`(myCoursesIds)
    }

    private fun containKeyword(keyword: String?): BooleanExpression? {
        if (keyword == null) {
            return null
        }

        return course.title.contains(keyword)
    }

    private fun isInCampus(campusId: Long?): BooleanExpression? {
        if (campusId == null) {
            return null
        }

        return course.id.eq(campusId)
    }

}