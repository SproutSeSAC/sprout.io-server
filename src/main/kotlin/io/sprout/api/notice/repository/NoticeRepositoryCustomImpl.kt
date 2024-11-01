package io.sprout.api.notice.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.dto.NoticeUrlInfo
import io.sprout.api.notice.model.entities.QNoticeEntity
import io.sprout.api.notice.model.entities.QNoticeEntity.noticeEntity
import io.sprout.api.notice.model.entities.QScrapedNoticeEntity.scrapedNoticeEntity
import io.sprout.api.user.model.entities.QUserEntity.userEntity
import org.springframework.data.domain.PageRequest

class NoticeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeRepositoryCustom {

    override fun filterNotices(filterRequest: NoticeFilterRequest, userId: Long): Pair<List<NoticeResponseDto>, Long> {
        // BooleanBuilder로 필터 조건 생성
        val builder = createFilterBuilder(filterRequest, userId)

        // 정렬 조건 설정
        val orderSpecifier = getOrderSpecifier(filterRequest.sort, noticeEntity)

        // 1. 부모 공지사항만 페이징하여 조회 (부모 ID가 null인 것들)
        val pageable = PageRequest.of(filterRequest.page - 1, filterRequest.size)
        val parentNotices = queryFactory
            .from(noticeEntity)
            .leftJoin(noticeEntity.writer, userEntity)
            .leftJoin(scrapedNoticeEntity)
            .on(
                scrapedNoticeEntity.notice.id.eq(noticeEntity.id)
                    .and(scrapedNoticeEntity.user.id.eq(userId))
            )
            .where(builder.and(noticeEntity.parentId.isNull)) // 부모 ID가 null인 공지사항만 필터링
            .orderBy(orderSpecifier)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .transform(
                groupBy(noticeEntity.id).list(
                    Projections.constructor(
                        NoticeResponseDto::class.java,
                        noticeEntity.id,
                        noticeEntity.title,
                        noticeEntity.content,
                        userEntity.nickname, // 작성자 이름
                        userEntity.profileImageUrl, // 작성자 프로필 이미지 URL
                        noticeEntity.startDate,
                        noticeEntity.endDate,
                        noticeEntity.status,
                        noticeEntity.noticeType,
                        noticeEntity.createdAt,
                        noticeEntity.updatedAt,
                        noticeEntity.participantCapacity,
                        noticeEntity.viewCount,
                        scrapedNoticeEntity.id.isNotNull, // 스크랩 여부를 Boolean 타입으로 변환
                        noticeEntity.parentId, // parentId 포함
                        list(
                            Projections.constructor(
                                NoticeUrlInfo::class.java,
                                noticeEntity.url,
                                noticeEntity.startDate,
                                noticeEntity.endDate,
                                noticeEntity.subtitle,
                                noticeEntity.parentId
                            )
                        )
                    )
                )
            )
        val parentIds = parentNotices.map { it.id }
        val children = queryFactory
            .select(
                Projections.constructor(
                    NoticeUrlInfo::class.java,
                    noticeEntity.url,
                    noticeEntity.startDate,
                    noticeEntity.endDate,
                    noticeEntity.subtitle,
                    noticeEntity.parentId
                )
            )
            .from(noticeEntity)
            .where(noticeEntity.parentId.`in`(parentIds))
            .orderBy(noticeEntity.id.asc())
            .fetch()

        parentNotices.forEach { parent ->
            val matchingChildren = children.filter { it.parentId == parent.id }
            parent.children.addAll(matchingChildren)
        }




        // 3. 전체 공지사항 수 계산 (필터링된 부모 공지사항의 총 개수)
        val totalCount = fetchTotalCount(builder, userId, filterRequest)

        // 결과를 Pair로 반환 (부모 공지사항 리스트, 전체 카운트)
        return Pair(parentNotices, totalCount)
    }

    // 필터 조건을 생성하는 메서드
    private fun createFilterBuilder(
        filterRequest: NoticeFilterRequest,
        userId: Long
    ): BooleanBuilder {
        val builder = BooleanBuilder()

        // 키워드 필터링 조건 추가
        filterRequest.keyword?.let {
            builder.and(noticeEntity.title.containsIgnoreCase(it).or(noticeEntity.content.containsIgnoreCase(it)))
        }

        // 공지 유형 필터링 조건 추가
        filterRequest.noticeType?.let {
            builder.and(noticeEntity.noticeType.eq(it))
        }

        // 스크랩 여부 필터링 조건 추가
        if (filterRequest.onlyScraped) {
            builder.and(scrapedNoticeEntity.user.id.eq(userId))
        }

        return builder
    }

    // 정렬 조건을 설정하는 메서드
    private fun getOrderSpecifier(sort: String, notice: QNoticeEntity) =
        when (sort) {
            "popular" -> notice.viewCount.desc() // 인기순 정렬
            else -> notice.createdAt.desc() // 최신순 정렬 (기본값)
        }

    // 전체 공지사항 수를 계산하는 메서드
    private fun fetchTotalCount(
        builder: BooleanBuilder,
        userId: Long,
        filterRequest: NoticeFilterRequest
    ): Long {
        val noticeEntity = noticeEntity
        val scrapedNoticeEntity = scrapedNoticeEntity

        // 기본 쿼리: 필터링된 공지사항의 개수 조회
        val query = queryFactory
            .select(noticeEntity.count())
            .from(noticeEntity)

        // 스크랩 필터링이 있는 경우에만 스크랩 테이블과 조인
        if (filterRequest.onlyScraped) {
            query.leftJoin(scrapedNoticeEntity).on(scrapedNoticeEntity.notice.id.eq(noticeEntity.id))
        }

        return query
            .where(builder)
            .fetchOne() ?: 0L // 결과가 null인 경우 0으로 반환
    }
}
