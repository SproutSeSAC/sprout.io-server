package io.sprout.api.notice.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.notice.model.dto.NoticeJoinRequestListDto
import io.sprout.api.notice.model.entities.QNoticeEntity.noticeEntity
import io.sprout.api.notice.model.entities.QNoticeJoinRequestEntity.noticeJoinRequestEntity
import io.sprout.api.user.model.entities.QUserEntity.userEntity

class NoticeJoinRequestRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeJoinRequestRepositoryCustom {
    override fun getRequestList(noticeId: Long) :List<NoticeJoinRequestListDto> {
       return queryFactory.select(
            Projections.constructor(
                NoticeJoinRequestListDto::class.java,
                    noticeJoinRequestEntity.id,
                    userEntity.name,
                    userEntity.nickname,
                    userEntity.id,
                    noticeJoinRequestEntity.notice.id,
                    noticeEntity.subtitle,
                    noticeJoinRequestEntity.createdAt
                )
        ).from(noticeJoinRequestEntity)
            .leftJoin(noticeJoinRequestEntity.user,userEntity)
            .leftJoin(noticeJoinRequestEntity.notice,noticeEntity)
            .where(noticeJoinRequestEntity.notice.id.eq(noticeId))
            .orderBy(noticeJoinRequestEntity.id.desc())
            .fetch()

    }
}