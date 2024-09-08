package io.sprout.api.user.infrastructure.repository

import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {

    fun findByEmail(email: String): Optional<UserEntity>
}