package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeSessionEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface NoticeSessionRepository : JpaRepository<NoticeSessionEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT session " +
            "FROM NoticeSessionEntity session " +
            "WHERE session.id = :sessionId")
    fun findByIdWithLock(sessionId: Long): NoticeSessionEntity?

    @Query("SELECT s FROM NoticeSessionEntity s WHERE s.eventStartDateTime BETWEEN :now AND :future30")
    fun findSessionsAfter(now: LocalDateTime, future30: LocalDateTime): List<NoticeSessionEntity>

    @Query("SELECT s FROM NoticeSessionEntity s WHERE s.eventEndDateTime BETWEEN :past30 AND :now")
    fun findSessionsBefore(now: LocalDateTime, past30: LocalDateTime): List<NoticeSessionEntity>

}