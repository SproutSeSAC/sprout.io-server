package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.UserDomainEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserDomainRepository: JpaRepository<UserDomainEntity, Long> {
}