package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*

/**
 * 공지사항 세션 참여자
 * (참가 / 대기) 상태 존재
 */
@Entity
@Table(name = "notice_participant")
class NoticeParticipantEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long,

    @Enumerated(EnumType.STRING)
    var status: ParticipantStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user : UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_session_id", nullable = false)
    val noticeSession : NoticeSessionEntity
)

enum class ParticipantStatus{
    WAIT,
    PARTICIPANT
}

