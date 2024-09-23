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
): StoreRepositoryCustom {

    // 여기서 필터링 필요
    override fun findStoreList(request: StoreDto.StoreListRequest): List<StoreProjectionDto.StoreInfoDto> {
        val store = QStoreEntity.storeEntity
        val storeImage = QStoreImageEntity.storeImageEntity
        val storeMenu = QStoreMenuEntity.storeMenuEntity

        return jpaQueryFactory
            .selectFrom(store)
            .leftJoin(storeMenu).on(store.id.eq(storeMenu.store.id))
            .leftJoin(storeImage).on(store.id.eq(storeImage.store.id))
            .where(
                searchInZeropay(request.isZeropay),
                searchInWalkTime(request.walkTimeWithinFiveMinutes),
                searchInFoodType(request.foodTypeList)
            )
            .orderBy(store.id.asc())
            .distinct()
            .transform(
                groupBy(store.id).list(
                    Projections.constructor(
                        StoreProjectionDto.StoreInfoDto::class.java,
                        store.id,
                        store.name,
                        store.address,
                        store.contact,
                        store.foodType,
                        store.workingDay,
                        store.breakTime,
                        store.holiday,
                        store.isZeropay,
                        store.walkTime,
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
    }

    // Dynamic predicates
    private fun searchInZeropay(isZeropay: Boolean?): BooleanExpression? {
        return if (isZeropay == true) {
            QStoreEntity.storeEntity.isZeropay.eq(true)
        } else if (isZeropay == false){
            QStoreEntity.storeEntity.isZeropay.eq(false)
        } else {
            null
        }
    }

    private fun searchInWalkTime(walkTimeWithinFiveMinutes: Boolean?):  BooleanExpression? {
        return if (walkTimeWithinFiveMinutes == true) {
            QStoreEntity.storeEntity.walkTime.loe(5)
        } else {
            null
        }
    }

    private fun searchInFoodType(foodTypeList: List<FoodType>):  BooleanExpression? {
        return if (foodTypeList.isEmpty()) {
            null
        } else {
            QStoreEntity.storeEntity.foodType.`in`(foodTypeList)
        }
    }



}