package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.entities.MeetingType
import io.sprout.api.project.model.entities.QProjectEntity
import org.springframework.stereotype.Repository

@Repository
class ProjectCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProjectCustomRepository {
    override fun filterProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long> {
        val projectEntity = QProjectEntity.projectEntity
        val builder = BooleanBuilder()

        // 기술 스택 필터링
        if (!filterRequest.techStack.isNullOrEmpty()) {
            builder.and(projectEntity.techStacks.any().techStack.id.`in`(filterRequest.techStack))
        }

        // 포지션 필터링
        if (!filterRequest.position.isNullOrEmpty()) {
            builder.and(projectEntity.positions.any().position.id.`in`(filterRequest.position))
        }

        // 진행 방식 필터링
        if (!filterRequest.meetingType.isNullOrEmpty()) {
            builder.and(projectEntity.meetingType.eq(MeetingType.valueOf(filterRequest.meetingType.uppercase())))
        }

        // 총 갯수 계산
        val totalCount = queryFactory
            .select(projectEntity.count())
            .from(projectEntity)
            .where(builder)
            .fetchOne()

        // 필터링된 프로젝트 목록 가져오기 (id 기준 내림차순 정렬하고 20개만 반환)
        val projects = queryFactory
            .selectFrom(projectEntity)
            .where(builder)
            .orderBy(projectEntity.id.desc())  // id 순으로 내림차순 정렬
            .limit(20)  // 최대 20개 결과 반환
            .fetch()

        // Entity를 DTO로 변환하여 반환
        val projectDtos = projects.map { project ->
            ProjectResponseDto(
                id = project.id,
                title = project.title,
                description = project.description,
                recruitmentCount = project.recruitmentCount,
                meetingType = project.meetingType.name,
                contactMethod = project.contactMethod.name,
                recruitmentStart = project.recruitmentStart,
                recruitmentEnd = project.recruitmentEnd
            )
        }

        // 프로젝트 목록과 총 갯수를 함께 반환
        return Pair(projectDtos, totalCount ?: 0L)
    }
}
