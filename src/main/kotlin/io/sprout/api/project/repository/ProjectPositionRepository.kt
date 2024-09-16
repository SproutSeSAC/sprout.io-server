package io.sprout.api.project.repository

import io.sprout.api.project.model.entities.ProjectPositionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPositionRepository : JpaRepository<ProjectPositionEntity, Long>{
}