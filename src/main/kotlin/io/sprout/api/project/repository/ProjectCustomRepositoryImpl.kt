package io.sprout.api.project.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.post.entities.QPostEntity
import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.*
import io.sprout.api.scrap.entity.QScrapEntity
import io.sprout.api.specification.model.entities.QJobEntity
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

    override fun findProjectDetailById(id: Long, userId: Long): ProjectDetailResponseDto? {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
//        val positionEntity = QPositionEntity.positionEntity
        val positionEntity = QJobEntity.jobEntity
        val userEntity = QUserEntity.userEntity
        val techStackEntity = QTechStackEntity.techStackEntity
        val projectTechStackEntity = QProjectTechStackEntity.projectTechStackEntity

        // 1. 프로젝트 정보 조회
        val project = queryFactory
            .selectFrom(projectEntity)
            .leftJoin(projectEntity.writer, userEntity).fetchJoin()
            .leftJoin(projectEntity.positions, projectPositionEntity).fetchJoin()
            .leftJoin(projectPositionEntity.position, positionEntity).fetchJoin()
            .leftJoin(projectEntity.techStacks , projectTechStackEntity).fetchJoin()
            .leftJoin(projectTechStackEntity.techStack, techStackEntity).fetchJoin()
            .where(projectEntity.id.eq(id))
            .fetchOne()

        // 프로젝트가 없으면 null 반환
        project ?: return null

        // 2. 스크랩 여부 조회
        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity

        val isScraped = queryFactory
            .selectOne()
            .from(postEntity)
            .leftJoin(scrapEntity)
            .on(scrapEntity.postId.eq(postEntity.id)
                .and(scrapEntity.userId.eq(userId)))
            .where(
                postEntity.linkedId.eq(id)
                    .and(scrapEntity.id.isNotNull)
            )
            .fetchFirst() != null

        // 3. 엔티티를 DTO로 변환하고, 스크랩 여부 설정
        return project.toDto().apply {
            this.isScraped = isScraped
            this.position = project.positions.map { it.position }
            this.techStack = project.techStacks.map { it.techStack }
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

    override fun findProjectsEndingCloseWithDetails(size: Long, days: Long): List<ProjectSimpleResponseDto> {
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
            .where(project.recruitmentEnd.between(
                LocalDate.now(), 
                LocalDate.now().plusDays(days)))
            .orderBy(project.recruitmentEnd.asc())
            .limit(size)
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
        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity
//        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity

        filterRequest.techStack?.let {
            builder.and(projectEntity.techStacks.any().techStack.id.`in`(it))
        }

        filterRequest.position?.let {
            builder.and(projectEntity.positions.any().position.id.`in`(it))
        }

        filterRequest.meetingType?.let {
            builder.and(projectEntity.meetingType.eq(MeetingType.valueOf(it.uppercase())))
        }

        filterRequest.pType?.let {
            builder.and(projectEntity.pType.eq(it))
        }

        if (filterRequest.onlyScraped) {
            builder.and(scrapEntity.userId.eq(userId))
//            builder.and(scrapedProjectEntity.user.id.eq(userId))
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
//        val scrapedProjectEntity = QScrapedProjectEntity.scrapedProjectEntity

        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity

        val query = queryFactory
            .select(projectEntity.count())
            .from(projectEntity)

        // 스크랩 필터링이 있는 경우에만 조인
        if (filterRequest.onlyScraped) {
            query.leftJoin(postEntity).on(postEntity.linkedId.eq(projectEntity.id))
            query.leftJoin(scrapEntity)
                .on(scrapEntity.postId.eq(postEntity.id)
                    .and(scrapEntity.userId.eq(userId)))
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
        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity

        val orderSpecifier = when (filterRequest.sort) {
            "popularity" -> projectEntity.viewCount.desc()
            else -> projectEntity.id.desc()
        }

        val query = queryFactory
            .select(projectEntity.id)
            .from(projectEntity)
            .leftJoin(postEntity)
            .on(postEntity.linkedId.eq(projectEntity.id))
            .leftJoin(scrapEntity)
            .on(scrapEntity.postId.eq(postEntity.id))
            .where(builder)
            .orderBy(orderSpecifier)
            .limit(filterRequest.size.toLong())
            .offset((filterRequest.page - 1).toLong() * filterRequest.size.toLong())

        if (filterRequest.onlyScraped) {
            query.where(scrapEntity.id.isNotNull)
        }

        return query.fetch()
    }


    private fun fetchProjectDetails(
        filterRequest: ProjectFilterRequest,
        projectIds: List<Long>,
        userId: Long
    ): List<ProjectResponseDto> {
        val projectEntity = QProjectEntity.projectEntity
        val projectPositionEntity = QProjectPositionEntity.projectPositionEntity
        val positionEntity = QJobEntity.jobEntity
        val projectTechStackEntity = QProjectTechStackEntity.projectTechStackEntity
        val techStackEntity = QTechStackEntity.techStackEntity

        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity

        // 정렬 조건
        val orderSpecifier = if (filterRequest.sort == "popularity") {
            projectEntity.viewCount.desc()
        } else {
            projectEntity.id.desc()
        }

        val query = queryFactory
            .select(projectEntity)
            .from(projectEntity)
            .leftJoin(projectEntity.techStacks, projectTechStackEntity)
            .leftJoin(projectTechStackEntity.techStack, techStackEntity)
            .leftJoin(projectEntity.positions, projectPositionEntity)
            .leftJoin(projectPositionEntity.position, positionEntity)
            .leftJoin(postEntity)
            .on(postEntity.linkedId.eq(projectEntity.id))
            .leftJoin(scrapEntity)
            .on(scrapEntity.postId.eq(postEntity.id)
                .and(scrapEntity.userId.eq(userId)))
            .where(projectEntity.id.`in`(projectIds))

        // 스크랩 여부 처리
        if (filterRequest.onlyScraped) {
            query.where(scrapEntity.id.isNotNull)
        }

        // 필드 구성
        val result = query
            .orderBy(orderSpecifier)
            .transform(
                GroupBy.groupBy(projectEntity.id).list(
                    Projections.constructor(
                        ProjectResponseDto::class.java,
                        projectEntity.id,
                        projectEntity.title,
                        projectEntity.projectStatus,
                        projectEntity.description,
                        projectEntity.recruitmentCount,
                        projectEntity.meetingType.stringValue(),
                        projectEntity.contactMethod.stringValue(),
                        projectEntity.recruitmentStart,
                        projectEntity.recruitmentEnd,
                        projectEntity.pType.stringValue(),
                        GroupBy.list(projectPositionEntity.position.name),
                        GroupBy.list(
                            Projections.constructor(
                                ProjectResponseDto.TechStacks::class.java,
                                projectTechStackEntity.techStack.name,
                                projectTechStackEntity.techStack.path
                            )
                        ),
                        scrapEntity.id.isNotNull,
                        projectEntity.viewCount
                    )
                )
            )

        return result
    }
}
