package io.sprout.api.project.repository

import io.sprout.api.project.model.entities.ProjectEntity
import io.sprout.api.project.model.entities.ScrapedProjectEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrapedProjectRepository : JpaRepository<ScrapedProjectEntity, Long> {
    fun findByUserAndProject(user: UserEntity, project: ProjectEntity): ScrapedProjectEntity?
}