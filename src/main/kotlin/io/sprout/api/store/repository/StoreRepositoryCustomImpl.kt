package io.sprout.api.store.repository

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto
import io.sprout.api.store.model.entities.FoodType
import io.sprout.api.store.model.entities.QStoreEntity
import io.sprout.api.store.model.entities.QStoreImageEntity
import io.sprout.api.store.model.entities.QStoreMenuEntity

class StoreRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : StoreRepositoryCustom {

    override fun findStoreList(request: StoreDto.StoreListRequest): Pair<List<StoreProjectionDto.StoreInfoDto>, Long> {
        val store = QStoreEntity.storeEntity
        val storeImage = QStoreImageEntity.storeImageEntity
        val storeMenu = QStoreMenuEntity.storeMenuEntity

        val totalCount = jpaQueryFactory
            .select(store.id)
            .from(store)
            .leftJoin(storeMenu).on(store.id.eq(storeMenu.store.id))
            .leftJoin(storeImage).on(store.id.eq(storeImage.store.id))
            .where(
                searchInZeropay(request.isZeropay),
                searchInWalkTime(request.walkTimeWithinFiveMinutes),
                searchInFoodType(request.foodTypeList),
                searchInPerson(request.overFivePerson),
                searchInUnderPrice(request.underPrice),
            )
            .groupBy(store.id)
            .fetch()

        val storeIdsForPagination= jpaQueryFactory
            .select(store.id)
            .from(store)
            .orderBy(store.id.asc())
            .limit(request.size.toLong())
            .offset((request.page * request.size).toLong())
            .fetch()

        val result = jpaQueryFactory
            .selectFrom(store)
            .leftJoin(storeMenu).on(store.id.eq(storeMenu.store.id))
            .leftJoin(storeImage).on(store.id.eq(storeImage.store.id))
            .where(
                store.id.`in`(storeIdsForPagination),
                searchInZeropay(request.isZeropay),
                searchInWalkTime(request.walkTimeWithinFiveMinutes),
                searchInFoodType(request.foodTypeList),
                searchInPerson(request.overFivePerson),
                searchInUnderPrice(request.underPrice),
            )
            .orderBy(store.id.asc())
            .distinct()
            .transform(
                groupBy(store.id).list(
                    Projections.constructor(
                        StoreProjectionDto.StoreInfoDto::class.java,
                        store.id,
                        store.name,
                        store.mapSchemaUrl,
                        store.address,
                        store.contact,
                        store.foodType,
                        store.workingDay,
                        store.breakTime,
                        store.holiday,
                        store.isZeropay,
                        store.walkTime,
                        store.isOverPerson,
                        list(
                            Projections.constructor(
                                StoreProjectionDto.StoreImageDto::class.java,
                                storeImage.id,
                                storeImage.path
                            )
                        ),
                        list(
                            Projections.constructor(
                                StoreProjectionDto.StoreMenuDto::class.java,
                                storeMenu.id,
                                storeMenu.name,
                                storeMenu.price
                            )
                        )
                    )
                )
            )

        return Pair(result, totalCount.size.toLong())
    }

    override fun findStoreFilterList(): List<StoreProjectionDto.StoreFilterDto> {
        val store = QStoreEntity.storeEntity
        val storeMenu = QStoreMenuEntity.storeMenuEntity

        return jpaQueryFactory
            .selectFrom(store)
            .leftJoin(storeMenu).on(store.id.eq(storeMenu.store.id))
            .distinct()
            .transform(
                groupBy(store.id).list(
                    Projections.constructor(
                        StoreProjectionDto.StoreFilterDto::class.java,
                        store.id,
                        store.name,
                        store.foodType,
                        store.isZeropay,
                        store.walkTime,
                        store.isOverPerson,
                        list(
                            Projections.constructor(
                                StoreProjectionDto.StoreMenuDto::class.java,
                                storeMenu.id,
                                storeMenu.name,
                                storeMenu.price
                            )
                        )
                    )
                )
            )
    }

    // Dynamic predicates
    private fun searchInZeropay(isZeropay: Boolean): BooleanExpression? {
        return if (isZeropay) {
            QStoreEntity.storeEntity.isZeropay.eq(true)
        } else {
            null
        }
    }

    private fun searchInWalkTime(walkTimeWithinFiveMinutes: Boolean): BooleanExpression? {
        return if (walkTimeWithinFiveMinutes) {
            QStoreEntity.storeEntity.walkTime.loe(5)
        } else {
            null
        }
    }

    private fun searchInFoodType(foodTypeList: MutableSet<FoodType>): BooleanExpression? {
        return if (foodTypeList.isNotEmpty()) {
            QStoreEntity.storeEntity.foodType.`in`(foodTypeList)
        } else {
            null
        }
    }

    private fun searchInPerson(overFivePerson: Boolean): BooleanExpression? {
        return if (overFivePerson) {
            QStoreEntity.storeEntity.isOverPerson.eq(true)
        } else {
            null
        }
    }

    private fun searchInUnderPrice(underPrice: Boolean): BooleanExpression? {
        return if (underPrice) {
            QStoreMenuEntity.storeMenuEntity.price.loe(10000)
        } else {
            null
        }
    }

}