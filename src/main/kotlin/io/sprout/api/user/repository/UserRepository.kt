package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, Long> , UserRepositoryCustom{

    fun findByEmail(email: String): UserEntity?
    fun findByRefreshToken(token: String?): UserEntity?

    @EntityGraph(attributePaths = ["userJobList", "userDomainList", "userTechStackList"])
    fun findUserById(userId: Long): UserEntity?
    fun findByNickname(nickname: String): UserEntity?
    fun findByRole(admin: RoleType): List<UserEntity>

}