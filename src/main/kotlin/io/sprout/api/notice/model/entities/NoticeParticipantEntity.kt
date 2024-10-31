package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "notice_participant")
class NoticeParticipantEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long ,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user : UserEntity ,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice : NoticeEntity
):BaseEntity()


