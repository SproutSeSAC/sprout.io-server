package io.sprout.api.project.repository

import io.sprout.api.project.model.entities.ProjectCommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectCommentRepository : JpaRepository<ProjectCommentEntity, Long> {
}