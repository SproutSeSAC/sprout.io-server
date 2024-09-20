package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.position.model.entities.QPositionEntity
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.dto.QProjectResponseDto
import io.sprout.api.project.model.entities.MeetingType
import io.sprout.api.project.model.entities.QProjectEntity
import io.sprout.api.project.model.entities.QProjectPositionEntity
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Repository

@Repository
class ProjectCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProjectCustomRepository {

    override fun filterProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long> {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
        val positionEntity = QPositionEntity.positionEntity

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
            .leftJoin(projectEntity.positions, projectPositionEntity)
            .leftJoin(projectPositionEntity.position, positionEntity)
            .where(projectEntity.id.`in`(projectIds))  // 첫 번째 쿼리에서 가져온 ID 목록을 사용
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
                        GroupBy.list(projectPositionEntity.position.name)  // 포지션 이름을 리스트로 묶음
                    )
                )
            )



        return Pair(projects, totalCount ?: 0L)
    }
}


