package io.sprout.api.notice.repository

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.course.model.entities.QCourseEntity
import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.*
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.entities.QPostEntity
import io.sprout.api.scrap.entity.QScrapEntity
import io.sprout.api.user.model.entities.QUserCourseEntity
import io.sprout.api.user.model.entities.QUserEntity
import io.sprout.api.user.model.entities.RoleType
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class NoticeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeRepositoryCustom {
    val notice = QNoticeEntity.noticeEntity
    val noticeSession = QNoticeSessionEntity.noticeSessionEntity
    val noticeParticipant = QNoticeParticipantEntity.noticeParticipantEntity
    val targetCourse = QNoticeTargetCourseEntity.noticeTargetCourseEntity
    val scrapedNotice = QScrapedNoticeEntity.scrapedNoticeEntity
    val userCourse = QUserCourseEntity.userCourseEntity
    val course = QCourseEntity.courseEntity
    val user = QUserEntity.userEntity

    val post = QPostEntity.postEntity
    val scrap = QScrapEntity.scrapEntity

    /**
     * 공지사항 ID에 해당하는 세션 정보, 세션 참가자수, 자신의 참가정보를 반환한다.
     *
     * @param noticeId 공지사항 ID
     * @param userId 회원 ID
     */
    override fun findByIdWithSession(noticeId: Long, userId: Long): List<NoticeDetailResponseDto.Session> {
        return queryFactory
            .select(
                Projections.constructor(
                    NoticeDetailResponseDto.Session::class.java,
                    noticeSession.id,
                    noticeSession.eventStartDateTime,
                    noticeSession.eventEndDateTime,
                    noticeSession.ordinal,
                    JPAExpressions
                        .select(noticeParticipant.id.count())
                        .from(noticeParticipant)
                        .where(
                            noticeParticipant.noticeSession.id.eq(noticeSession.id),
                            noticeParticipant.status.eq(ParticipantStatus.PARTICIPANT)
                        ),
                    JPAExpressions
                        .select(noticeParticipant.status)
                        .from(noticeParticipant)
                        .where(
                            noticeParticipant.noticeSession.id.eq(noticeSession.id),
                            noticeParticipant.user.id.eq(userId)
                        )
                    )
            )
            .from(noticeSession)
            .leftJoin(noticeSession.noticeParticipants, noticeParticipant)
                .on(noticeParticipant.noticeSession.id.eq(noticeSession.id))
            .where(noticeSession.notice.id.eq(noticeId))
            .groupBy(noticeSession.id)
            .fetch()
    }

    /**
     * 공지사항 검색
     *
     * @param searchRequest 공지사항 검색 파라미터
     */
    override fun search(searchRequest: NoticeSearchRequestDto, userId: Long): MutableList<NoticeSearchDto> {
        val myCourseIds: List<Long> = queryFactory
            .select(userCourse.course.id)
            .from(userCourse)
            .where(userCourse.user.id.eq(userId))
            .fetch()

        val ids = getSearchedIds(userId, myCourseIds, searchRequest)

        val result = getNoticeDetail(userId, ids)

        return result
    }

    override fun getSessions(userId: Long, pageable: PageRequest, applicationStatus: NoticeStatus?, keyword: String?): NoticeSessionResponseDto {
        val myCourseIds: List<Long> = queryFactory
            .select(userCourse.course.id)
            .from(userCourse)
            .where(userCourse.user.id.eq(userId))
            .fetch()

        val ids = queryFactory
            .selectDistinct(noticeSession.id)
            .from(noticeSession)
            .leftJoin(noticeSession.notice, notice)
            .leftJoin(notice.user, user)
            .leftJoin(notice.targetCourses, targetCourse)
            .leftJoin(targetCourse.course, course)
            .where(
                isInCourse(myCourseIds),
                isNoticeStatus(applicationStatus),
                likeNoticeTitle(keyword)
            )
            .orderBy(OrderSpecifier(Order.DESC, noticeSession.eventStartDateTime))
            .limit(pageable.pageSize.toLong() + 1L)
            .offset(pageable.offset)
            .fetch()

        // size result
        // 5    6       X           remove
        // 5    5       last page   X
        // 5    3       last page   X
        val isLastPage = pageable.pageSize + 1 > ids.size
        if (ids.size == pageable.pageSize + 1) {
            ids.removeLast()
        }

        val result = queryFactory
            .selectFrom(noticeSession)
            .leftJoin(noticeSession.notice, notice)
            .leftJoin(notice.user, user)
            .leftJoin(notice.targetCourses, targetCourse)
            .leftJoin(targetCourse.course, course)
            .leftJoin(post)
                .on(post.linkedId.eq(notice.id).and(post.postType.eq(PostType.NOTICE)))

            .where(noticeSession.id.`in`(ids))
            .orderBy(OrderSpecifier(Order.DESC, noticeSession.eventStartDateTime))
            .transform(
                groupBy(noticeSession.id).list(
                    Projections.constructor(
                        NoticeSessionResponseDto.NoticeSessionCard::class.java,
                        post.id,
                        notice.id,
                        notice.title,
                        notice.noticeType,
                        notice.status,
                        notice.createdAt,
                        notice.applicationStartDateTime,
                        notice.applicationEndDateTime,
                        notice.meetingPlace,
                        notice.meetingType,
                        notice.participantCapacity,
                        Projections.constructor(
                            NoticeSessionResponseDto.Writer::class.java,
                            user.id,
                            user.name,
                            user.profileImageUrl,
                            user.role
                        ),
                        list(Projections.constructor(
                            NoticeSessionResponseDto.TargetCourse::class.java,
                            course.id,
                            course.title
                        )),
                        Projections.constructor(
                            NoticeSessionResponseDto.Session::class.java,
                            noticeSession.id,
                            noticeSession.eventStartDateTime,
                            noticeSession.eventEndDateTime,
                            noticeSession.ordinal
                        ),
                    )
                )
            )

        return NoticeSessionResponseDto(result, isLastPage)
    }

    /**
     * 마감 하루전날인 공지사항 검색
     */
    override fun getApplicationCloseNotice(userId: Long, size: Long, days: Long): MutableList<NoticeCardDto>? {
        val myCourseIds: List<Long> = queryFactory
            .select(userCourse.course.id)
            .from(userCourse)
            .where(userCourse.user.id.eq(userId))
            .fetch()

        val ids = queryFactory
            .selectDistinct(notice.id)
            .from(notice)
            .leftJoin(notice.user, user)
            .leftJoin(notice.targetCourses, targetCourse)
            .leftJoin(targetCourse.course, course)
            .where(
                isInCourse(myCourseIds),
                notice.applicationEndDateTime.between(
                    LocalDateTime.now(),
                    LocalDateTime.of(LocalDate.now().plusDays(days), LocalTime.MAX))
            )
            .orderBy(notice.applicationEndDateTime.asc())
            .limit(size)
            .fetch()

        val result = queryFactory
            .selectFrom(notice)
            .leftJoin(notice.user, user)
            .leftJoin(post)
                .on(post.linkedId.eq(notice.id).and(
                    post.postType.eq(PostType.NOTICE)))
            .where(notice.id.`in`(ids))
            .orderBy(notice.applicationEndDateTime.asc())
            .transform(
                groupBy(notice.id).list(
                    Projections.constructor(
                        NoticeCardDto::class.java,
                        post.id,
                        notice.id,
                        notice.title,
                        notice.applicationEndDateTime,

                        Projections.constructor(
                            NoticeCardDto.Manager::class.java,
                            user.id,
                            user.name,
                            user.nickname,
                            user.role
                        )
                    )
                )
            )

        return result
    }

    private fun getNoticeDetail(
        userId: Long,
        ids: MutableList<Long>?
    ): MutableList<NoticeSearchDto> {
        val result = queryFactory
            .select(notice.id)
            .from(notice)
            .leftJoin(notice.user, user)
            .leftJoin(notice.targetCourses, targetCourse)
            .leftJoin(targetCourse.course, course)
            .leftJoin(scrapedNotice)
            .on(
                scrapedNotice.notice.id.eq(notice.id)
                    .and(scrapedNotice.user.id.eq(userId))
            )
            .where(notice.id.`in`(ids))
            .orderBy(OrderSpecifier(Order.DESC, notice.createdAt))
            .transform(
                groupBy(notice.id).list(
                    Projections.constructor(
                        NoticeSearchDto::class.java,
                        notice.id,
                        user.id,
                        user.name,
                        user.role,
                        notice.title,
                        notice.content.substring(0, 150),
                        notice.content.length().gt(150),
                        notice.status,
                        notice.viewCount,
                        notice.noticeType,
                        notice.createdAt,
                        notice.updatedAt,
                        JPAExpressions
                            .selectOne()
                            .from(scrapedNotice)
                            .where(
                                scrapedNotice.notice.id.eq(notice.id),
                                scrapedNotice.user.id.eq(userId)
                            ).exists(),
                        list(course.title)
                    )
                )
            )

        return result
    }

    private fun getSearchedIds(
        userId: Long,
        myCourseIds: List<Long>,
        searchRequest: NoticeSearchRequestDto
    ): MutableList<Long>? {
        val query = queryFactory
            .select(notice.id)
            .from(notice)
            .leftJoin(notice.user, user)
            .leftJoin(notice.targetCourses, targetCourse)
            .leftJoin(targetCourse.course, course)
            .leftJoin(post)
            .on(post.linkedId.eq(notice.id))
            .where(
                isInCourse(myCourseIds),
                containKeyword(searchRequest.keyword),
                isWriterRoleType(searchRequest.roleType),
                isNoticeType(searchRequest.noticeType)
            )

        if (searchRequest.onlyScraped == true) {
            query.leftJoin(scrap)
                .on(scrap.postId.eq(post.id)
                    .and(scrap.userId.eq(userId)))
            query.where(scrap.id.isNotNull)
        }

        return query
            .groupBy(notice.id)
            .orderBy(OrderSpecifier(Order.DESC, notice.createdAt))
            .limit(searchRequest.size.toLong() + 1)
            .offset(searchRequest.offset.toLong())
            .fetch()
    }

    private fun isInCourse(myCoursesIds: List<Long>): BooleanExpression? {
        return course.id.`in`(myCoursesIds)
    }

    private fun containKeyword(keyword: String?): BooleanExpression? {
        if (keyword == null) {
            return null
        }

        return notice.title.contains(keyword)
            .or(notice.content.contains(keyword))
    }


    private fun isWriterRoleType(roleType: RoleType?): BooleanExpression? {
        if (roleType == null) {
            return null
        }

        return user.role.eq(roleType)
    }

    private fun isOnlyScraped(onlyScraped: Boolean?, memberId: Long): BooleanExpression? {
        if (onlyScraped== null || ! onlyScraped) {
            return null
        }

        return scrapedNotice.user.id.`in`(memberId)
    }

    private fun isNoticeType(noticeType: NoticeType?): BooleanExpression? {
        if (noticeType == null) {
            return null
        }

        return notice.noticeType.eq(noticeType)
    }

    private fun isNoticeStatus(status: NoticeStatus?): BooleanExpression? {
        if (status == null) {
            return null
        }

        return notice.status.eq(status)
    }

    private fun likeNoticeTitle(keyword: String?): BooleanExpression? {
        if (keyword == null) {
            return null
        }

        return notice.title.contains(keyword)
    }
}







