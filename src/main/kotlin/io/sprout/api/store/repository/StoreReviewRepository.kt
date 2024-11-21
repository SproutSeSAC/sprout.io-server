package io.sprout.api.store.repository

import io.sprout.api.store.model.entities.StoreReviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreReviewRepository: JpaRepository<StoreReviewEntity, Long> {

}