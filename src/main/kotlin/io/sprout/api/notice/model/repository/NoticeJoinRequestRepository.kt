package io.sprout.api.notice.model.repository

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeJoinRequestEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeJoinRequestRepository :JpaRepository<NoticeJoinRequestEntity, Long>{
    fun findByUserAndNotice(user: UserEntity, notice: NoticeEntity): NoticeJoinRequestEntity?
}