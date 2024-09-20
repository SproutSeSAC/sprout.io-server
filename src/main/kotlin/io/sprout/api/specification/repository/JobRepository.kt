package io.sprout.api.specification.repository

import io.sprout.api.specification.model.entities.JobEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface JobRepository: JpaRepository<JobEntity, Long> {
}