package io.sprout.api.notice.model.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.model.entities.QNoticeEntity.noticeEntity
import io.sprout.api.user.model.entities.QUserEntity.userEntity

class NoticeRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : NoticeRepositoryCustom {
    override fun findByNoticeType(noticeType: NoticeType?): List<NoticeEntity> {
        val query = jpaQueryFactory
            .selectFrom(noticeEntity)
            .join(noticeEntity.writer, userEntity).fetchJoin()

        // noticeType이 null이 아닌 경우에만 조건 추가
        if (noticeType != null) {
            query.where(noticeEntity.noticeType.eq(noticeType))
        }

        return query.fetch()
    }
}