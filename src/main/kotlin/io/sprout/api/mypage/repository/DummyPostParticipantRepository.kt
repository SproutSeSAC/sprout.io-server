package io.sprout.api.mypage.repository

import io.sprout.api.mypage.entity.DummyPostParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyPostParticipantRepository : JpaRepository<DummyPostParticipant, Int> {
    fun findAllByUserId(userId: Int): List<DummyPostParticipant>

    fun deleteByPostparticipantidAndUserId(postparticipantid: Int, userId: Int): Int
}
