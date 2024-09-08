package io.sprout.api.user.infra

import io.sprout.api.user.domain.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {

    fun findByEmail(email: String): UserEntity?
}