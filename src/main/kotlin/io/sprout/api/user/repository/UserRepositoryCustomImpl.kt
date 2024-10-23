package io.sprout.api.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.user.model.entities.GoogleCalendarEntity
import io.sprout.api.user.model.entities.QGoogleCalendarEntity
import io.sprout.api.user.model.entities.QUserEntity
import io.sprout.api.user.model.entities.RoleType

class UserRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
): UserRepositoryCustom {
    override fun findUsersWithCalendarByRole(roleType: RoleType): List<GoogleCalendarEntity> {
        val user = QUserEntity.userEntity
        val googleCalendar = QGoogleCalendarEntity.googleCalendarEntity

        return jpaQueryFactory
            .select(googleCalendar)
            .from(googleCalendar)
            .leftJoin(googleCalendar.user, user)
            .fetchJoin() // 유저 정보를 함께 조회
            .where(user.role.eq(roleType))
            .fetch()
    }
}