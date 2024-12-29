package io.sprout.api.mypage.repository

import io.sprout.api.mypage.entity.DummyPostParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyPostParticipantRepository : JpaRepository<DummyPostParticipant, Long> {
    fun findAllByUserId(userId: Long): List<DummyPostParticipant>

    fun deleteByPostparticipantidAndUserId(postparticipantid: Int, userId: Long): Int
}
