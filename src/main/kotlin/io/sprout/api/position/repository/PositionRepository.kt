package io.sprout.api.position.repository

import io.sprout.api.position.model.entities.PositionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PositionRepository : JpaRepository<PositionEntity, Long> {
}