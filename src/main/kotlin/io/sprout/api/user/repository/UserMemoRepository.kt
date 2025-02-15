package io.sprout.api.user.repository

import io.sprout.api.user.model.entities.UserMemo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserMemoRepository: JpaRepository<UserMemo, Long> {
    fun findByUserIdAndTargetUserId(managerId: Long, traineeId: Long): UserMemo?
    fun deleteByUserIdAndTargetUserId(managerId: Long, traineeId: Long)
}