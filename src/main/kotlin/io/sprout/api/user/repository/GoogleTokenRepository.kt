package io.sprout.api.user.repository
import org.springframework.data.jpa.repository.JpaRepository
import io.sprout.api.user.model.entities.GoogleTokenEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.stereotype.Repository

@Repository
interface GoogleTokenRepository : JpaRepository<GoogleTokenEntity, Long> {
    fun findByUser(user: UserEntity): GoogleTokenEntity?
}
