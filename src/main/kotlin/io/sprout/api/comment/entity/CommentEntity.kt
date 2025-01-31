package io.sprout.api.comment.entity

import io.sprout.api.post.entities.PostEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
class CommentEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(nullable = false)
        var content: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        var user: UserEntity,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id", nullable = false)
        var post: PostEntity,

        @Column(name = "created_at", nullable = false)
        var createdAt: LocalDateTime,

        @Column(name = "img_url", nullable = false)
        var imgurl: String,
)