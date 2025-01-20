package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeSessionEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoticeSessionRepository : JpaRepository<NoticeSessionEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT session " +
            "FROM NoticeSessionEntity session " +
            "WHERE session.id = :sessionId")
    fun findByIdWithLock(sessionId: Long): NoticeSessionEntity?
}