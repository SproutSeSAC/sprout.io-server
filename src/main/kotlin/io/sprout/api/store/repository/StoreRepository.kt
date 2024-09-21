package io.sprout.api.store.repository

import io.sprout.api.store.model.entities.StoreEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository: JpaRepository<StoreEntity, Long>, StoreRepositoryCustom {
}