package io.sprout.api.project.repository

import io.sprout.api.project.model.entities.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long>, ProjectCustomRepository {

}