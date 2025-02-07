package io.sprout.api.user.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPAExpressions.select
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.campus.model.entities.QCampusEntity
import io.sprout.api.common.model.entities.PageResponse
import io.sprout.api.course.model.entities.QCourseEntity
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.specification.model.entities.QDomainEntity
import io.sprout.api.specification.model.entities.QJobEntity
import io.sprout.api.specification.model.entities.QTechStackEntity
import io.sprout.api.user.model.dto.UserSearchRequestDto
import io.sprout.api.user.model.dto.UserSearchResponseDto
import io.sprout.api.user.model.entities.QUserCourseEntity
import io.sprout.api.user.model.entities.QUserDomainEntity
import io.sprout.api.user.model.entities.QUserEntity
import io.sprout.api.user.model.entities.QUserJobEntity
import io.sprout.api.user.model.entities.QUserTechStackEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity

class UserRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): UserRepositoryCustom {

    private val userEntity = QUserEntity.userEntity
    private val userCourseEntity = QUserCourseEntity.userCourseEntity
    private val courseEntity = QCourseEntity.courseEntity
    private val campusEntity = QCampusEntity.campusEntity
    private val userTechStackEntity = QUserTechStackEntity.userTechStackEntity
    private val techStackEntity = QTechStackEntity.techStackEntity
    private val userDomainEntity = QUserDomainEntity.userDomainEntity
    private val domainEntity = QDomainEntity.domainEntity
    private val userJobEntity = QUserJobEntity.userJobEntity
    private val jobEntity = QJobEntity.jobEntity

    override fun findManagerEmailSameCourse(courseId: Long): List<UserEntity> {

        val userCourseEntity = QUserCourseEntity("userCourseEntity")

        return jpaQueryFactory
            .select(userEntity)
            .from(userEntity)
            .leftJoin(userEntity.userCourseList, QUserCourseEntity.userCourseEntity)
            .where(
                userCourseEntity.course.id.eq(courseId)
                    .and(
                        userEntity.role.eq(RoleType.CAMPUS_LEADER)
                            .or(userEntity.role.eq(RoleType.JOB_COORDINATOR))
                    )
            )
            .fetch()
    }

    override fun search(searchRequest: UserSearchRequestDto, user: UserEntity): PageResponse<UserSearchResponseDto> {
        // mycourse 찾기
        val myCourseIds = jpaQueryFactory
            .select(userCourseEntity.course.id)
            .from(userEntity)
            .leftJoin(userEntity.userCourseList, userCourseEntity)
            .where(userEntity.id.eq(user.id))
            .fetch()

        val totalCount = jpaQueryFactory.select(userEntity.id.count())
            .from(userEntity)
            .where(createFilterBuilder(searchRequest, myCourseIds))
            .orderBy(userEntity.createdAt.desc())
            .limit(searchRequest.size)
            .offset(searchRequest.getOffset())
            .fetchOne()

        // 검색 Id만 찾기 - 캠퍼스 Id, courseId, keyword-name, mycourse in
        val resultIds = jpaQueryFactory
            .selectFrom(userEntity)
            .where(createFilterBuilder(searchRequest, myCourseIds))
            .orderBy(userEntity.createdAt.desc())
            .limit(searchRequest.size)
            .offset(searchRequest.getOffset())
            .transform(GroupBy.groupBy(userEntity.id).list(
                    userEntity.id
            ))

        val result = jpaQueryFactory
            .selectFrom(userEntity)
            .leftJoin(userEntity.userCourseList, userCourseEntity)
            .leftJoin(userCourseEntity.course, courseEntity)
            .leftJoin(courseEntity.campus, campusEntity)
            .where(userEntity.id.`in`(resultIds))
            .transform(
                GroupBy.groupBy(userEntity.id).list(
                    Projections.constructor(
                        UserSearchResponseDto::class.java,
                        userEntity.id,
                        userEntity.name,
                        userEntity.email,
                        userEntity.phoneNumber,
                        GroupBy.list(
                            Projections.constructor(
                                UserSearchResponseDto.Campus::class.java,
                                campusEntity.id,
                                campusEntity.name
                            )
                        ),
                        GroupBy.list(
                            Projections.constructor(
                                UserSearchResponseDto.Course::class.java,
                                courseEntity.id,
                                courseEntity.title
                            )
                        ),
                    )
                )
            )

        val techStacks = jpaQueryFactory
            .selectFrom(userEntity)
            .innerJoin(userEntity.userTechStackList, userTechStackEntity)
            .innerJoin(userTechStackEntity.techStack, techStackEntity)
            .where(userEntity.id.`in`(resultIds))
            .transform(
                GroupBy.groupBy(userEntity.id)
                    .`as`(
                        GroupBy.list(
                            Projections.constructor(
                                UserSearchResponseDto.TechStack::class.java,
                                techStackEntity.id,
                                techStackEntity.name
                            )
                        )
                    )
            )

        val jobs = jpaQueryFactory
            .selectFrom(userEntity)
            .innerJoin(userEntity.userJobList, userJobEntity)
            .innerJoin(userJobEntity.job, jobEntity)
            .where(userEntity.id.`in`(resultIds))
            .transform(
                GroupBy.groupBy(userEntity.id)
                    .`as`(
                        GroupBy.list(
                            Projections.constructor(
                                UserSearchResponseDto.Job::class.java,
                                jobEntity.id,
                                jobEntity.name
                            )
                        )
                    )
            )

        val domains = jpaQueryFactory
            .selectFrom(userEntity)
            .innerJoin(userEntity.userDomainList, userDomainEntity)
            .innerJoin(userDomainEntity.domain, domainEntity)
            .where(userEntity.id.`in`(resultIds))
            .transform(
                GroupBy.groupBy(userEntity.id)
                    .`as`(
                        GroupBy.list(
                            Projections.constructor(
                                UserSearchResponseDto.Domain::class.java,
                                domainEntity.id,
                                domainEntity.name
                            )
                        )
                    )
            )

        result.forEach {
            it.distinctCampus()
            it.domains = domains[it.userId] ?: mutableListOf()
            it.jobs = jobs[it.userId] ?: mutableListOf()
            it.techStacks = techStacks[it.userId] ?: mutableListOf()
        }

        return PageResponse(result, totalCount ?: 0)
    }


    private fun createFilterBuilder(
        searchRequest: UserSearchRequestDto,
        myCourseIds: MutableList<Long>
    ): BooleanBuilder {
        val builder = BooleanBuilder()

        searchRequest.keyword?.let {
            builder.and(userEntity.name.eq(it))
        }
        searchRequest.campusId?.let {
            builder.and(userEntity.userCourseList.any().course.campus.id.`in`(it))
        }
        searchRequest.courseId?.let {
            builder.and(userEntity.userCourseList.any().course.id.`in`(it))
        }
        myCourseIds.let {
            builder.and(userEntity.userCourseList.any().course.id.`in`(it))
        }
        return builder
    }

}