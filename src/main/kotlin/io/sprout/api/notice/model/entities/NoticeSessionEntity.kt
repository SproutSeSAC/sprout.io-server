package io.sprout.api.notice.model.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

/**
 * 특정 공지사항(특강, 행사)의 교육 세션 엔티티
 */
@Entity
@Table(name = "notice_session")
class NoticeSessionEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice : NoticeEntity,

    val eventStartDateTime: LocalDateTime,
    val eventEndDateTime: LocalDateTime,

    val ordinal: Int

){
    @OneToMany(mappedBy = "noticeSession", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val noticeParticipants : MutableSet<NoticeParticipantEntity> = mutableSetOf()

    /**
     * notice SessionId 만을 가지고있는 dummy constructor
     */
    constructor(noticeSessionId: Long): this(
        id = noticeSessionId,
        notice = NoticeEntity(-1),
        eventEndDateTime = LocalDateTime.now(),
        eventStartDateTime = LocalDateTime.now(),
        ordinal = 1,
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoticeSessionEntity) return false

        return eventStartDateTime == other.eventStartDateTime
                && eventEndDateTime == other.eventEndDateTime
                && notice.id == other.notice.id
    }

    override fun hashCode(): Int {
        return Objects.hash(
            notice.id,
            eventStartDateTime.toString(),
            eventEndDateTime.toString())
    }
}