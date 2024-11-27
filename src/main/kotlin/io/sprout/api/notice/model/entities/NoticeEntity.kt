package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
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

    @Enumerated(EnumType.STRING)
    var meetingType: NoticeMeetingType,

    @Column(nullable = false)
    var viewCount: Int = 0,

    val meetingPlace: String? = null,

    val applicationForm: String? = null,

    val applicationStartDateTime: LocalDateTime? = null,

    val applicationEndDateTime: LocalDateTime? = null,

    val participantCapacity: Int? = null,

    val satisfactionSurvey: String? = null,


    ) : BaseEntity() {


    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val noticeSessions : MutableList<NoticeSessionEntity> = mutableListOf()

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val targetCourses : MutableList<NoticeTargetCourseEntity> = mutableListOf()

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