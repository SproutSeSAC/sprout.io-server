package io.sprout.api.campus.infra

import io.sprout.api.campus.model.entities.CampusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampusRepository: JpaRepository<CampusEntity, Long> {

}