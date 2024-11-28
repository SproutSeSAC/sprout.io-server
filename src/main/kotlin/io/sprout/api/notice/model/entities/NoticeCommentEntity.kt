package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*

/**
 * 특강 댓글 엔티티
 */
@Entity
@Table(name = "notice_comment")
class NoticeCommentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    val content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice: NoticeEntity,
):BaseEntity() {
}