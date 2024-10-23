package io.sprout.api.notice.model.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.model.entities.QNoticeEntity
import io.sprout.api.notice.model.entities.QNoticeEntity.*
import io.sprout.api.notice.model.entities.QScrapedNoticeEntity
import io.sprout.api.notice.model.entities.QScrapedNoticeEntity.scrapedNoticeEntity
import io.sprout.api.user.model.entities.QUserEntity
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

        // 쿼리 실행 (페이징을 적용하지 않고 전체 카운트를 먼저 구함)
        val totalCount = fetchTotalCount(builder, userId, filterRequest)

        // 페이지네이션 설정
        val pageable = PageRequest.of(filterRequest.page - 1, filterRequest.size)

        // 페이징 적용하여 필터링된 결과 가져오기
        val resultList = queryFactory
            .select(
                Projections.constructor(
                    NoticeResponseDto::class.java,
                    noticeEntity.id,
                    noticeEntity.title,
                    noticeEntity.content,
                    userEntity.nickname,
                    userEntity.profileImageUrl,
                    noticeEntity.startDate,
                    noticeEntity.endDate,
                    noticeEntity.status,
                    noticeEntity.noticeType,
                    noticeEntity.createdAt,
                    noticeEntity.updatedAt,
                    noticeEntity.viewCount,
                    scrapedNoticeEntity.id.isNotNull // 스크랩 여부
                )
            )
            .from(noticeEntity)
            .leftJoin(noticeEntity.writer,userEntity)
            .leftJoin(scrapedNoticeEntity)
            .on(
                scrapedNoticeEntity.notice.id.eq(noticeEntity.id).
                and(scrapedNoticeEntity.user.id.eq(userId))
            )
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 결과를 Pair로 반환 (공지사항 리스트, 전체 카운트)
        return Pair(resultList, totalCount)
    }


    private fun createFilterBuilder(
        filterRequest: NoticeFilterRequest,
        userId: Long
    ): BooleanBuilder {
        val builder = BooleanBuilder()

        // 키워드 필터링
        filterRequest.keyword?.let {
            builder.and(noticeEntity.title.containsIgnoreCase(it).or(noticeEntity.content.containsIgnoreCase(it)))
        }

        // 공지 유형 필터링
        filterRequest.noticeType?.let {
            builder.and(noticeEntity.noticeType.eq(it))
        }

        // 스크랩 여부 필터링
        if (filterRequest.onlyScraped) {
            builder.and(scrapedNoticeEntity.user.id.eq(userId))
        }

        return builder
    }

    // 정렬 조건을 설정하는 함수
    private fun getOrderSpecifier(sort: String, notice: QNoticeEntity) =
        when (sort) {
            "popular" -> notice.viewCount.desc() // 인기순 정렬
            else -> notice.createdAt.desc() // 최신순 정렬 (기본값)
        }

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