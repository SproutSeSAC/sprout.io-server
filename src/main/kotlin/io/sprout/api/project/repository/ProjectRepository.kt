package io.sprout.api.project.repository

import io.sprout.api.project.model.entities.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ProjectRepository : JpaRepository<ProjectEntity, Long>, ProjectCustomRepository {
    @Query("SELECT p FROM ProjectEntity p WHERE p.recruitmentEnd = :yesterday")
    fun findProjectsEndingYesterday(yesterday: LocalDate): List<ProjectEntity>
}