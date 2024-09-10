package io.sprout.api.user.infra

import io.sprout.api.user.model.entities.UserJobEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserJobRepository: JpaRepository<UserJobEntity, Long> {
}