package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.UserTechStackEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserTechStackRepository: JpaRepository<UserTechStackEntity, Long> {
}