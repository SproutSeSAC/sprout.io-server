package io.sprout.api.project.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "project_comment")
class ProjectCommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val content: String, // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val writer: UserEntity, // 작성자 (유저와의 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: ProjectEntity, // 프로젝트와의 관계
): BaseEntity()