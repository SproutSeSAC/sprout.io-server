package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.*

/**
 * 특정 공지사항(특강, 행사)의 교육 세션 엔티티
 */
@Entity
@Table(name = "notice_session")
class NoticeSessionEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice : NoticeEntity,

    val eventStartDateTime: LocalDateTime,
    val eventEndDateTime: LocalDateTime,

){
    @OneToMany(mappedBy = "noticeSession", fetch = FetchType.LAZY)
    val noticeParticipants : MutableSet<NoticeParticipantEntity> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoticeSessionEntity) return false

        return eventStartDateTime == other.eventStartDateTime
                && eventEndDateTime == other.eventEndDateTime
                && notice.id == other.notice.id
    }

    // TODO hashCode에서 notice.id를 참조하는것이 올바른가? 에 대해서 생각을 해보는것이...
    //  하지만 kotlin에서 not null을 강제해주는데 맞을지도?
    override fun hashCode(): Int {
        return Objects.hash(
            notice.id,
            eventStartDateTime.toString(),
            eventEndDateTime.toString())
    }
}