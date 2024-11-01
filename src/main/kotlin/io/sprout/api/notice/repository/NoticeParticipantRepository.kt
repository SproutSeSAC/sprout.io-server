package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeParticipantRepository : JpaRepository<NoticeParticipantEntity, Long> {
    fun findByUserAndNotice(user: UserEntity, notice: NoticeEntity): NoticeParticipantEntity?
}