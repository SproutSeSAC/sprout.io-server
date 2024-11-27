package io.sprout.api.notice.repository

import com.querydsl.jpa.impl.JPAQueryFactory

class NoticeRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : NoticeRepositoryCustom {
}
