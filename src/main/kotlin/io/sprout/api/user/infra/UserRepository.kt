package io.sprout.api.user.infra

import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {

    fun findByEmail(email: String): UserEntity?
    fun findByRefreshToken(token: String?): UserEntity?
}