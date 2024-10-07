package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.position.model.entities.QPositionEntity
import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.*
import io.sprout.api.specification.model.entities.QTechStackEntity
import io.sprout.api.user.model.entities.QUserEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

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

    override fun findProjectDetailById(id: Long , userId: Long): ProjectDetailResponseDto? {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
        val positionEntity = QPositionEntity.positionEntity
        val userEntity = QUserEntity.userEntity

        // 1. 프로젝트 정보 조회
        val project = queryFactory
            .selectFrom(projectEntity)
            .leftJoin(projectEntity.writer, userEntity).fetchJoin()
            .leftJoin(projectEntity.positions, projectPositionEntity).fetchJoin()
            .leftJoin(projectPositionEntity.position, positionEntity).fetchJoin()
            .where(projectEntity.id.eq(id))
            .fetchOne()

        // 프로젝트가 없으면 null 반환
        project ?: return null

        // 2. 스크랩 여부 조회
        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity
        val isScraped = queryFactory
            .selectOne()
            .from(scrapedProjectEntity)
            .where(scrapedProjectEntity.project.id.eq(id)
                .and(scrapedProjectEntity.user.id.eq(userId)))
            .fetchFirst() != null // 스크랩한 기록이 있는지 여부를 확인

        // 3. 엔티티를 DTO로 변환하고, 스크랩 여부 설정
        return project.toDto().apply {
            this.isScraped = isScraped
        }


    }

    override fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto> {
        val projectComment = QProjectCommentEntity.projectCommentEntity
        val user = QUserEntity.userEntity
        val project = QProjectEntity.projectEntity

        // QueryDSL을 사용하여 ProjectCommentResponseDto 리스트를 가져오는 쿼리
        return queryFactory
            .select(
                Projections.constructor(
                    ProjectCommentResponseDto::class.java,
                    projectComment.id,
                    projectComment.content,
                    projectComment.createdAt,
                    user.nickname, // 닉네임
                    project.id,
                    user.profileImageUrl
                )
            )
            .from(projectComment)
            .leftJoin(projectComment.writer, user) // 작성자와 조인
            .leftJoin(projectComment.project, project) // 프로젝트와 조인
            .where(project.id.eq(projectId)) // 프로젝트 ID 필터링
            .orderBy(projectComment.createdAt.asc()) // 생성일자 오름차순 정렬
            .fetch()
    }

    override fun findProjectsEndingTommorowWithDetails(tomorrow: LocalDate): List<ProjectSimpleResponseDto> {
        val project = QProjectEntity.projectEntity
        val user = QUserEntity.userEntity

        return queryFactory
            .select(
                project.id,
                project.title,
                project.description,
                user.nickname,
                user.profileImageUrl
            )
            .from(project)
            .join(project.writer, user)
            .where(project.recruitmentEnd.eq(tomorrow))
            .fetch()
            .map { tuple ->
                ProjectSimpleResponseDto(
                    projectId = tuple.get(project.id) ?: throw IllegalArgumentException("Project ID cannot be null"),
                    title = tuple.get(project.title) ?: "",
                    content = tuple.get(project.description) ?: "",
                    userNickname = tuple.get(user.nickname) ?: "Unknown",
                    imgUrl = tuple.get(user.profileImageUrl) ?: "null",
                )
            }
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
            .on(
                scrapedProjectEntity.project.id.eq(projectEntity.id)
                    .and(scrapedProjectEntity.user.id.eq(userId))
            )
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
