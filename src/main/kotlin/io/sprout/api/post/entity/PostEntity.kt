package io.sprout.api.post.entity

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
    var referenceId: Long
)

enum class PostType {
    PROJECT,
    NOTICE
}