package io.sprout.api.mealPost.repository

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.model.entities.MealPostStatus
import io.sprout.api.mealPost.model.entities.QMealPostEntity
import io.sprout.api.mealPost.model.entities.QMealPostParticipationEntity
import io.sprout.api.user.model.entities.QUserEntity
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class MealPostRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : MealPostRepositoryCustom {
    val mealPost: QMealPostEntity = QMealPostEntity.mealPostEntity
    val mealPostParticipant: QMealPostParticipationEntity = QMealPostParticipationEntity.mealPostParticipationEntity
    val user: QUserEntity = QUserEntity.userEntity
    val hostUser: QUserEntity = QUserEntity.userEntity


    override fun findMealPostList(pageable: Pageable, userId: Long): List<MealPostProjection> {
        val fetch: List<MealPostProjection> = jpaQueryFactory
            .select(
                Projections.constructor(
                    MealPostProjection::class.java,
                    mealPost.id,
                    mealPost.title,
                    mealPost.appointmentTime,
                    mealPost.storeName,
                    mealPost.meetingPlace,
                    mealPost.memberCount,
                    mealPostParticipant.id.count(),

                    JPAExpressions.select(user.nickname)
                        .from(mealPostParticipant)
                        .join(mealPostParticipant.user, user)
                        .where(mealPostParticipant.ordinalNumber.eq(1).and(mealPostParticipant.mealPost.id.eq(mealPost.id))),
                    JPAExpressions.select(user.profileImageUrl)
                        .from(mealPostParticipant)
                        .join(mealPostParticipant.user, user)
                        .where(mealPostParticipant.ordinalNumber.eq(1).and(mealPostParticipant.mealPost.id.eq(mealPost.id))),

                    JPAExpressions.select(mealPostParticipant.user.id)
                        .from(mealPostParticipant)
                        .where(
                            mealPostParticipant.mealPost.id.eq(mealPost.id)
                                .and(mealPostParticipant.user.id.eq(userId))
                        ).exists(),
                )
            )

            .from(mealPost)
            .leftJoin(mealPost.mealPostParticipationList, mealPostParticipant)
            .where(
                mealPost.appointmentTime.goe(LocalDateTime.now()),
                mealPost.mealPostStatus.eq(MealPostStatus.ACTIVE),
            )
            .orderBy(OrderSpecifier(Order.ASC, mealPost.appointmentTime))
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .groupBy(mealPost.id)
            .fetch()

        return fetch

    }
}