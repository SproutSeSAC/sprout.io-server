package io.sprout.api.post.entity

import io.sprout.api.comment.entity.CommentEntity
import jakarta.persistence.*

@Entity
@Table(name = "post")
class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    var postType: PostType,

    @Column(name = "reference_id", nullable = false)
    var referenceId: Long,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<CommentEntity> = mutableListOf()
)

enum class PostType {
    PROJECT,
    NOTICE
}