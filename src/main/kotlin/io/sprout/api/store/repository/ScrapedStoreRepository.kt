package io.sprout.api.store.repository

import io.sprout.api.store.model.entities.ScrapedStoreEntity
import io.sprout.api.store.model.entities.StoreEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrapedStoreRepository: JpaRepository<ScrapedStoreEntity, Long> {
    fun findByUserAndStore(user: UserEntity, store: StoreEntity): ScrapedStoreEntity?
    fun findByUserIdAndStoreId(userId: Long, storeId: Long): ScrapedStoreEntity?
}