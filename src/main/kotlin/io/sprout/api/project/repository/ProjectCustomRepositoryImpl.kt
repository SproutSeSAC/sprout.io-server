package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.position.model.entities.QPositionEntity
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.entities.*
import io.sprout.api.specification.model.entities.QTechStackEntity
import org.springframework.stereotype.Repository

@Repository
class ProjectCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProjectCustomRepository {

    override fun filterProjects(
        filterRequest: ProjectFilterRequest,
        userId: Long
    ): Pair<List<ProjectResponseDto>, Long> {
        val builder = createFilterBuilder(filterRequest, userId)

        // 스크랩 여부에 따라 조인
        val totalCount = fetchTotalCount(builder, userId, filterRequest)
        val projectIds = fetchProjectIds(filterRequest, builder)
        val projects = fetchProjectDetails(filterRequest, projectIds, userId)

        val distinctProjects = projects
            .distinctBy { it.id }
            .map { it.toDistinct() }

        return Pair(distinctProjects, totalCount)
    }

    private fun createFilterBuilder(
        filterRequest: ProjectFilterRequest,
        userId: Long
    ): BooleanBuilder {
        val builder = BooleanBuilder()
        val projectEntity = QProjectEntity.projectEntity
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity

        filterRequest.techStack?.let {
            builder.and(projectEntity.techStacks.any().techStack.id.`in`(it))
        }

        filterRequest.position?.let {
            builder.and(projectEntity.positions.any().id.`in`(it))
        }

        filterRequest.meetingType?.let {
            builder.and(projectEntity.meetingType.eq(MeetingType.valueOf(it.uppercase())))
        }

        filterRequest.pType?.let {
            builder.and(projectEntity.pType.eq(it))
        }

        if (filterRequest.onlyScraped) {
            builder.and(scrapedProjectEntity.user.id.eq(userId))
        }

        filterRequest.keyWord?.let {
            builder.and(projectEntity.title.containsIgnoreCase(it)) // 대소문자 구분 없이 title 필터링
        }

        return builder
    }

    private fun fetchTotalCount(
        builder: BooleanBuilder,
        userId: Long,
        filterRequest: ProjectFilterRequest
    ): Long {
        val projectEntity = QProjectEntity.projectEntity
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity

        val query = queryFactory
            .select(projectEntity.count())
            .from(projectEntity)

        // 스크랩 필터링이 있는 경우에만 조인
        if (filterRequest.onlyScraped) {
            query.leftJoin(scrapedProjectEntity).on(scrapedProjectEntity.project.id.eq(projectEntity.id))
        }

        return query
            .where(builder)
            .fetchOne() ?: 0L
    }

    private fun fetchProjectIds(
        filterRequest: ProjectFilterRequest,
        builder: BooleanBuilder
    ): List<Long> {
        val projectEntity = QProjectEntity.projectEntity
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity

        val orderSpecifier = when (filterRequest.sort) {
            "popularity" -> projectEntity.viewCount.desc()
            else -> projectEntity.id.desc()
        }

        return queryFactory
            .select(projectEntity.id)
            .from(projectEntity)
            .apply {
                if (filterRequest.onlyScraped) {
                    leftJoin(scrapedProjectEntity).on(scrapedProjectEntity.project.id.eq(projectEntity.id))
                }
            }
            .where(builder)
            .orderBy(orderSpecifier)
            .limit(filterRequest.size.toLong())
            .offset((filterRequest.page - 1).toLong() * filterRequest.size.toLong())
            .fetch()
    }

    private fun fetchProjectDetails(
        filterRequest: ProjectFilterRequest,
        projectIds: List<Long>,
        userId: Long
    ): List<ProjectResponseDto> {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
        val positionEntity = QPositionEntity.positionEntity
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity
        val projectTechStackEntity = QProjectTechStackEntity.projectTechStackEntity
        val techStackEntity = QTechStackEntity.techStackEntity

        val orderSpecifier = if (filterRequest.sort == "popularity") {
            projectEntity.viewCount.desc()
        } else {
            projectEntity.id.desc()
        }

        return queryFactory
            .from(projectEntity)
            .leftJoin(projectEntity.techStacks, projectTechStackEntity)
            .leftJoin(projectTechStackEntity.techStack, techStackEntity)
            .leftJoin(projectEntity.positions, projectPositionEntity)
            .leftJoin(projectPositionEntity.position, positionEntity)
            .leftJoin(scrapedProjectEntity)
            .on(scrapedProjectEntity.project.id.eq(projectEntity.id)
                .and(scrapedProjectEntity.user.id.eq(userId)))
            .where(projectEntity.id.`in`(projectIds))
            .orderBy(orderSpecifier)
            .transform(
                GroupBy.groupBy(projectEntity.id).list(
                    Projections.constructor(
                        ProjectResponseDto::class.java,
                        projectEntity.id,
                        projectEntity.title,
                        projectEntity.description,
                        projectEntity.recruitmentCount,
                        projectEntity.meetingType.stringValue(),
                        projectEntity.contactMethod.stringValue(),
                        projectEntity.recruitmentStart,
                        projectEntity.recruitmentEnd,
                        projectEntity.pType.stringValue(),
                        GroupBy.list(projectPositionEntity.position.name),
                        GroupBy.list(projectTechStackEntity.techStack.name),
                        scrapedProjectEntity.id.isNotNull,
                        projectEntity.viewCount
                    )
                )
            )
    }
}
