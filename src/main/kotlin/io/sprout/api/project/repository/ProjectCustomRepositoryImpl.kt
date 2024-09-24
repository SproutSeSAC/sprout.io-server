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

        // 총 갯수 계산 (조인 없이 필터 조건에 맞는 프로젝트의 전체 개수만 계산)
        val totalCount = queryFactory
            .select(projectEntity.count())
            .from(projectEntity)
            .where(builder)
            .fetchOne()

        val projectIds = queryFactory
            .select(projectEntity.id)
            .from(projectEntity)
            .where(builder)
            .orderBy(projectEntity.id.desc())
            .limit(filterRequest.size.toLong())
            .offset((filterRequest.page * filterRequest.size).toLong())
            .fetch()


        // 프로젝트와 포지션 정보를 가져오기
        val projects = queryFactory
            .from(projectEntity)
            .leftJoin(projectEntity.techStacks, projectTechStackEntity) // Join project tech stacks
            .leftJoin(projectTechStackEntity.techStack, techStackEntity)
            .leftJoin(projectEntity.positions, projectPositionEntity)
            .leftJoin(projectPositionEntity.position, positionEntity)
            .leftJoin(scrapedProjectEntity)
            .where(projectEntity.id.`in`(projectIds))
            .on(scrapedProjectEntity.project.id.eq(projectEntity.id)
                .and(scrapedProjectEntity.user.id.eq(userId))) // 스크랩 여부 필터링// 첫 번째 쿼리에서 가져온 ID 목록을 사용
            .orderBy(projectEntity.id.desc())
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
                        scrapedProjectEntity.id.isNotNull,// 포지션 이름을 리스트로 묶음
                        projectEntity.viewCount
                    )
                )
            )


        val distinctProjects = projects.map { it.toDistinct() }

        return Pair(distinctProjects, totalCount ?: 0L)
    }
}


