package io.sprout.api.specification.repository

import io.sprout.api.specification.model.entities.TechStackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TechStackRepository: JpaRepository<TechStackEntity, Long> {
}