package io.sprout.api.mypage.repository

import io.sprout.api.user.model.entities.UserCampusEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserCampusRepository : JpaRepository<UserCampusEntity, Long> {
    fun findByUser_Id(userId: Long): Optional<UserCampusEntity>
}