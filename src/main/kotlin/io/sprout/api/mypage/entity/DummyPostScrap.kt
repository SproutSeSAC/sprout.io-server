package io.sprout.api.mypage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// 찜한 글
@Entity
@Table(name = "dummy_postscrap")
class DummyPostScrap (
        @Id
        @Column(nullable = false, length = 50)
        var postscrapid: Int,

        @Column(nullable = false)
        var userId: Int
) {
    constructor() : this(0, 0)
}