package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.position.model.entities.QPositionEntity
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.entities.*
import io.sprout.api.techStack.model.entities.QTechStackEntity
import org.springframework.stereotype.Repository

@Repository
class ProjectCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProjectCustomRepository {

    override fun filterProjects(
        filterRequest: ProjectFilterRequest,
        userId: Long
    ): Pair<List<ProjectResponseDto>, Long> {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
        val positionEntity = QPositionEntity.positionEntity
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity
        val projectTechStackEntity = QProjectTechStackEntity.projectTechStackEntity
        val techStackEntity = QTechStackEntity.techStackEntity
        val builder = BooleanBuilder()

        // 기술 스택 필터링
        if (!filterRequest.techStack.isNullOrEmpty()) {
            builder.and(projectEntity.techStacks.any().techStack.id.`in`(filterRequest.techStack))
        }

        // 포지션 필터링
        if (!filterRequest.position.isNullOrEmpty()) {
            builder.and(projectEntity.positions.any().id.`in`(filterRequest.position))
        }

        // 진행 방식 필터링
        if (!filterRequest.meetingType.isNullOrEmpty()) {
            builder.and(projectEntity.meetingType.eq(MeetingType.valueOf(filterRequest.meetingType.uppercase())))
        }

        // 프로젝트 타입 필터링 (pType에 따른 필터링)
        filterRequest.pType?.let {
            builder.and(projectEntity.pType.eq(it))
        }

        // 스크랩한 프로젝트만 필터링
        if (filterRequest.onlyScraped) {
            builder.and(scrapedProjectEntity.user.id.eq(userId))
        }

        // 총 갯수 계산
        val totalCount = queryFactory
            .select(projectEntity.count())
            .from(projectEntity)
            .leftJoin(scrapedProjectEntity).on(scrapedProjectEntity.project.id.eq(projectEntity.id))
            .where(builder)
            .fetchOne()

        // 동적으로 정렬 기준 설정
        val orderSpecifier = when (filterRequest.sort) {
            "popularity" -> projectEntity.viewCount.desc()  // 조회수 기준 내림차순 정렬
            "latest" -> projectEntity.id.desc()             // 기본값 최신순 (ID 내림차순)
            else -> projectEntity.id.desc()                 // 기본 정렬 기준
        }

        // 프로젝트 ID를 필터링
        val projectIds = queryFactory
            .select(projectEntity.id)
            .from(projectEntity)
            .apply {
                if (filterRequest.onlyScraped) {
                    // 스크랩된 프로젝트만 조회할 때 조인 추가
                    leftJoin(scrapedProjectEntity).on(scrapedProjectEntity.project.id.eq(projectEntity.id))
                }
            }
            .where(builder)
            .orderBy(orderSpecifier)  // 동적 정렬 조건
            .limit(filterRequest.size.toLong())
            .offset((filterRequest.page - 1).toLong() * filterRequest.size.toLong())
            .fetch()

        // 프로젝트와 포지션 정보를 가져오기
        val projects = queryFactory
            .from(projectEntity)
            .leftJoin(projectEntity.techStacks, projectTechStackEntity)
            .leftJoin(projectTechStackEntity.techStack, techStackEntity)
            .leftJoin(projectEntity.positions, projectPositionEntity)
            .leftJoin(projectPositionEntity.position, positionEntity)
            .leftJoin(scrapedProjectEntity)
            .on(scrapedProjectEntity.project.id.eq(projectEntity.id)
                .and(scrapedProjectEntity.user.id.eq(userId)))  // 스크랩 여부 필터링
            .where(projectEntity.id.`in`(projectIds))
            .orderBy(
                if (filterRequest.sort == "popularity") {
                    projectEntity.viewCount.desc()  // 조회수 기준 정렬 시 BooleanExpression 반환
                } else {
                    projectEntity.id.desc()  // 최신순 정렬 시 BooleanExpression 반환
                }
            )
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
                        scrapedProjectEntity.id.isNotNull,  // 스크랩 여부 확인
                        projectEntity.viewCount
                    )
                )
            )
        val filteredProjects = if (filterRequest.sort == "popularity") {
            projects.distinctBy { it.id }  // 프로젝트 ID를 기준으로 중복 제거
        } else {
            projects
        }
        val distinctProjects = filteredProjects.map { it.toDistinct() }

        return Pair(distinctProjects, totalCount ?: 0L)
    }
}
