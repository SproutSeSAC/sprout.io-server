package io.sprout.api.store.repository

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.ExpressionUtils.count
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.sprout.api.post.entities.QPostEntity
import io.sprout.api.scrap.entity.QScrapEntity
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto
import io.sprout.api.store.model.entities.*
import org.springframework.util.StringUtils

class StoreRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : StoreRepositoryCustom {

    override fun findStoreList(request: StoreDto.StoreListRequest, userId: Long): List<StoreProjectionDto.StoreInfoDto> {
        val store = QStoreEntity.storeEntity
        val storeImage = QStoreImageEntity.storeImageEntity
        val storeMenu = QStoreMenuEntity.storeMenuEntity
//        val storeScrap = QScrapedStoreEntity.scrapedStoreEntity

        val postEntity = QPostEntity.postEntity
        val scrapEntity = QScrapEntity.scrapEntity

        val result = jpaQueryFactory
            .selectFrom(store)
            .distinct()
            .leftJoin(storeMenu).on(store.id.eq(storeMenu.store.id)).fetchJoin()
            .where(
                searchInCampus(request.campusId),
                containKeywordsInName(request.keyword),
                searchInZeropay(request.isZeropay),
                searchInWalkTime(request.walkTimeWithinFiveMinutes),
                searchInFoodType(request.foodTypeList),
                searchInPerson(request.overFivePerson),
                searchInUnderPrice(request.underPrice),
            )
            .orderBy(store.id.asc())
            .limit(request.size.toLong())
            .offset((request.pageIndex * request.size).toLong())
            .transform(
                groupBy(store.id).list(
                    Projections.constructor(
                        StoreProjectionDto.StoreInfoDto::class.java,
                        store.id,
                        store.name,
                        store.foodType,
                        store.campus.name,
                        store.mapSchemaUrl,
                        store.address,
                        store.contact,
                        store.workingDay,
                        store.breakTime,
                        store.holiday,
                        store.isZeropay,
                        store.walkTime,
                        store.isOverPerson,
                        store.longitude,
                        store.latitude,
                        // 스크랩 수: store.id와 post.linkedId가 일치하고, 그 post에 연결된 scrap의 개수
                        ExpressionUtils.`as`(
                            JPAExpressions.select(count(scrapEntity.id))
                                .from(postEntity)
                                .leftJoin(scrapEntity)
                                .on(scrapEntity.postId.eq(postEntity.id))
                                .where(postEntity.linkedId.eq(store.id)),
                            "scrapCount"
                        ),
                        // 사용자가 스크랩했는지 여부: store.id와 post.linkedId가 일치하며, 그 post와 연결된 scrap에서 userId가 일치하는지
                        ExpressionUtils.`as`(
                            JPAExpressions.select(scrapEntity.id)
                                .from(postEntity)
                                .leftJoin(scrapEntity)
                                .on(scrapEntity.postId.eq(postEntity.id))
                                .where(
                                    postEntity.linkedId.eq(store.id)
                                        .and(scrapEntity.userId.eq(userId))
                                )
                                .exists(),
                            "isScrap"
                        )
                    )
                )
            )

        val searchedStoreIds: List<Long> = result.map { it.id }.toList()

        val images: List<StoreImageEntity> = jpaQueryFactory
            .select(storeImage)
            .from(storeImage)
            .distinct()
            .where(
                storeImage.store.id.`in`(searchedStoreIds)
            )
            .orderBy(storeImage.store.id.asc())
            .fetch()
        result.forEach {res -> images.forEach {img ->
                if (res.id == img.store.id) {
                    res.storeImageList.add(
                        StoreProjectionDto.StoreImageDto(img.id, img.path))
                }
            }
        }

        val menus: List<StoreMenuEntity> = jpaQueryFactory
            .select(storeMenu)
            .from(storeMenu)
            .distinct()
            .where(
                storeMenu.store.id.`in`(searchedStoreIds)
            )
            .orderBy(storeMenu.store.id.asc())
            .fetch()
        result.forEach {res -> menus.forEach {menu ->
                if (res.id == menu.store.id) {
                    res.storeMenuList.add(
                        StoreProjectionDto.StoreMenuDto(menu.id, menu.name, menu.price, menu.imageUrl))
                }
            }
        }

        return result
    }

    override fun findStoreFilterList(campusId: Long): StoreProjectionDto.StoreFilterCount {
        val store = QStoreEntity.storeEntity
        val storeMenu = QStoreMenuEntity.storeMenuEntity

        val foodTypeCount = jpaQueryFactory
            .select(Projections.constructor(
                StoreProjectionDto.FoodTypeCount::class.java,
                store.foodType,
                store.foodType.count()
            ))
            .from(store)
            .where(store.campus.id.eq(campusId))
            .groupBy(store.foodType)
            .fetch()

        val storeOptionCount = jpaQueryFactory
            .select(Projections.constructor(
                StoreProjectionDto.StoreOptionCount::class.java,
                JPAExpressions
                    .select(store.count())
                    .from(store)
                    .where(
                        store.campus.id.eq(campusId),
                        store.isZeropay.eq(true)
                    ),
                JPAExpressions
                    .select(store.count())
                    .from(store)
                    .where(
                        store.campus.id.eq(campusId),
                        store.isVoucher.eq(true)
                    ),
                JPAExpressions
                    .select(store.count())
                    .from(store)
                    .where(
                        store.campus.id.eq(campusId),
                        store.isOverPerson.eq(true)
                    )
            ))
            .from(store)
            .fetchFirst()!!

        val isLessThan10000Price = jpaQueryFactory
            .select(store.count())
            .from(store)
            .where(
                store.campus.id.eq(campusId)
                    .and(
                        JPAExpressions
                            .selectOne()
                            .from(storeMenu)
                            .where(
                                storeMenu.store.id.eq(store.id)
                                    .and(storeMenu.price.loe(10000))
                            )
                            .exists()
                    )
            )
            .fetchOne()!!

        storeOptionCount.isLessThan10000Price = isLessThan10000Price

        return StoreProjectionDto.StoreFilterCount(foodTypeCount, storeOptionCount)
    }

    // Dynamic predicates
    private fun searchInCampus(campusId: Long): BooleanExpression? {
        return QStoreEntity.storeEntity.campus.id.eq(campusId)
    }

    private fun containKeywordsInName(keyword: String): BooleanExpression? {
        return if (StringUtils.hasText(keyword)) {
            QStoreEntity.storeEntity.name.contains(keyword)
        } else {
            null
        }
    }

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