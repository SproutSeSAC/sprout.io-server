package io.sprout.api.mypage.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// 신청한 글
@Entity
@Table(name = "dummy_participant")
class DummyPostParticipant (
        @Id
        @Column(nullable = false, length = 50)
        var postparticipantId: Int,

        @Column(nullable = false)
        var userId: Int
) {
    constructor() : this(0, 0)
}