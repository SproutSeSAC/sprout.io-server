package io.sprout.api.techStack.repository

import io.sprout.api.techStack.model.entities.TechStackEntity
import io.sprout.api.user.model.entities.UserDomainEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TechStackRepository: JpaRepository<TechStackEntity, Long> {
}