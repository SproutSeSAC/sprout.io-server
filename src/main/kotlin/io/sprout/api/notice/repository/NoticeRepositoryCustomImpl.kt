package io.sprout.api.notice.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.notice.model.dto.NoticeDetailResponseDto
import io.sprout.api.notice.model.entities.ParticipantStatus
import io.sprout.api.notice.model.entities.QNoticeEntity
import io.sprout.api.notice.model.entities.QNoticeParticipantEntity
import io.sprout.api.notice.model.entities.QNoticeSessionEntity

class NoticeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeRepositoryCustom {
    val notice = QNoticeEntity.noticeEntity
    val noticeSession = QNoticeSessionEntity.noticeSessionEntity
    val noticeParticipant = QNoticeParticipantEntity.noticeParticipantEntity

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
}
