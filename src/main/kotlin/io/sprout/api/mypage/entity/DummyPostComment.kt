package io.sprout.api.mypage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// 댓글
@Entity
@Table(name = "dummy_post_comment")
class DummyPostComment (
        @Id
        @Column(nullable = false, length = 50)
        var commentId: Int,

        @Column(nullable = false)
        var userId: Int,

        @Column(nullable = false)
        var postId: Int
) {
    constructor() : this(0, 0, 0)
}