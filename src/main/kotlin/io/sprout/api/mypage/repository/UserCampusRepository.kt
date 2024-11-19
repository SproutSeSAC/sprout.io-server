package io.sprout.api.mypage.repository

import io.sprout.api.user.model.entities.UserCampusEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCampusRepository : JpaRepository<UserCampusEntity, Long>


