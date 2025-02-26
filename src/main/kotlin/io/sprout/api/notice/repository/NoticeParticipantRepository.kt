package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.model.entities.ParticipantStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface NoticeParticipantRepository : JpaRepository<NoticeParticipantEntity, Long> {
    fun findByNoticeSessionIdAndUserId(sessionId: Long, userId: Long): NoticeParticipantEntity?

    @Query("SELECT count(particiapnt) " +
            "FROM NoticeParticipantEntity particiapnt " +
            "WHERE particiapnt.noticeSession.id = :sessionId AND " +
            "   particiapnt.status = 'PARTICIPANT' ")
    fun countParticipantBySessionId(sessionId: Long) : Long
    fun findByIdAndNoticeSessionId(participantId: Long, sessionId: Long): NoticeParticipantEntity?

    @Query("SELECT participant " +
            "FROM NoticeParticipantEntity participant " +
            "LEFT JOIN FETCH participant.user user " +
            "LEFT JOIN FETCH user.userCourseList ucl " +
            "LEFT JOIN FETCH ucl.course course " +
            "LEFT JOIN FETCH course.campus " +
            "WHERE participant.noticeSession.id = :sessionId " +
            "   AND participant.status IN :searchParticipantStatus ")
    fun findBySessionIdAndStatusList(
        sessionId: Long,
        searchParticipantStatus: List<ParticipantStatus>,
        pageable: PageRequest
    ): Page<NoticeParticipantEntity>

//    @Query("""
//        SELECT participant
//        FROM NoticeParticipantEntity participant
//        LEFT JOIN FETCH participant.noticeSession session
//        WHERE session.eventEndDateTime <= :now
//        AND participant.status in ('P', )
//    """)
//    fun findByUserIdAndTimeIsBefore(now: LocalDateTime): List<NoticeParticipantEntity>

    @Modifying
    @Query("""
        UPDATE NoticeParticipantEntity participant
        SET participant.status = 'COMPLETE'
        WHERE participant.noticeSession.eventEndDateTime <= :now
        AND participant.status = 'PARTICIPANT'
    """)
    fun updateParticipantToCompleteAfter(now: LocalDateTime)

    @Modifying
    @Query("""
        UPDATE NoticeParticipantEntity participant
        SET participant.status = 'REJECT'
        WHERE participant.noticeSession.eventEndDateTime <= :now
        AND participant.status = 'WAIT'
    """)
    fun updateWaitToRejectAfter(now: LocalDateTime)
}