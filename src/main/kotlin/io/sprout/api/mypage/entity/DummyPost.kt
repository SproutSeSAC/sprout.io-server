package io.sprout.api.mypage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// 모든 글
@Entity
@Table(name = "dummy_post")
class DummyPost (
        @Id
        @Column(nullable = false, length = 50)
        var postid: Int,

        @Column(nullable = false)
        var userId: Int
) {
    constructor() : this(0, 0)
}