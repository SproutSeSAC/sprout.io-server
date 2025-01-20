package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 공지사항 엔티티 <br>
 * (특강/일반공지/행사/취업정보/기타) 유형 제공 <br>
 * 특강, 행사 유형일시 특강 Session 존재
 */
@Entity
@Table(name = "notice")
class NoticeEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    var status: NoticeStatus = NoticeStatus.ACTIVE,

    @Enumerated(EnumType.STRING)
    var noticeType: NoticeType,

    @Column(nullable = false)
    var viewCount: Int = 0,

    @Column(nullable = false)
    var isPhoneNumberRequired: Boolean = false,

    @Enumerated(EnumType.STRING)
    var meetingType: NoticeMeetingType?,

    var meetingPlace: String? = null,

    var applicationStartDateTime: LocalDateTime? = null,

    var applicationEndDateTime: LocalDateTime? = null,

    var participantCapacity: Int? = null,

    var satisfactionSurvey: String? = null,


    ) : BaseEntity() {
    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val noticeSessions : MutableSet<NoticeSessionEntity> = mutableSetOf()

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val targetCourses : MutableSet<NoticeTargetCourseEntity> = mutableSetOf()

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY)
    val noticeComments : MutableList<NoticeCommentEntity> = mutableListOf()

    constructor (noticeId :Long) : this(
        id = noticeId,
        title = "",
        content = "",
        user = UserEntity(0),
        status = NoticeStatus.ACTIVE,
        noticeType = NoticeType.GENERAL,
        viewCount = 0,
        meetingType = NoticeMeetingType.NONE
    )

    /**
     * 공지사항 update 로직
     * 각각의 파라미터들을 수정
     * 연관 엔티티는 PUT방식으로 비교하여 contain하지 않다면 삭제 & 동일하지 않은것은 추가하여 수정
     */
    fun update(noticeRequest: NoticeRequestDto) {
        this.title = noticeRequest.title
        this.content = noticeRequest.content
        this.noticeType = noticeRequest.noticeType
        this.isPhoneNumberRequired = noticeRequest.isPhoneNumberRequired
        this.applicationEndDateTime = noticeRequest.applicationEndDateTime
        this.applicationStartDateTime = noticeRequest.applicationStartDateTime
        this.participantCapacity = noticeRequest.participantCapacity
        this.meetingType = noticeRequest.meetingType
        this.meetingPlace = noticeRequest.meetingPlace
        this.satisfactionSurvey = noticeRequest.satisfactionSurvey

        val updateCourseList = noticeRequest.targetCourseIdList.map {
            NoticeTargetCourseEntity(
                notice = this,
                course = CourseEntity(it)) }

        val targetCourseIterator: MutableIterator<NoticeTargetCourseEntity> = targetCourses.iterator()
        while (targetCourseIterator.hasNext()) {
            val course = targetCourseIterator.next()
            if (! updateCourseList.contains(course)) {
                targetCourseIterator.remove()
            }
        }
        this.targetCourses.addAll(updateCourseList)

        val updateSessions = noticeRequest.sessions.map {
            NoticeSessionEntity(
                notice = this,
                eventStartDateTime = it.sessionStartDateTime,
                eventEndDateTime = it.sessionEndDateTime,
            )
        }
        val sessionIterator: MutableIterator<NoticeSessionEntity> = noticeSessions.iterator()
        while (sessionIterator.hasNext()) {
            val course = sessionIterator.next()
            if (! updateSessions.contains(course)) {
                sessionIterator.remove()
            }
        }
        this.noticeSessions.addAll(updateSessions)
    }

    /**
     * 조회수 증가
     */
    fun increaseViewCount() {
        viewCount += 1
    }
}

enum class NoticeStatus {
    ACTIVE,  // 현재 활성화된 공지사항
    INACTIVE,  // 비활성화된 공지사항
}

enum class NoticeMeetingType {
    OFFLINE,
    ONLINE,
    NONE
}



enum class NoticeType {
    SPECIAL_LECTURE,
    EMPLOYMENT,
    EVENT,
    GENERAL,
    ETC
}