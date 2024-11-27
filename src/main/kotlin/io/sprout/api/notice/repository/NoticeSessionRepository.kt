package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.model.entities.NoticeSessionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoticeSessionRepository : JpaRepository<NoticeSessionEntity, Long> {
}