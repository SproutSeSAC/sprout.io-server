package io.sprout.api.mypage.repository

import io.sprout.api.mypage.entity.DummyPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyPostRepository : JpaRepository<DummyPost, Int> {
    fun findAllByUserId(userId: Int): List<DummyPost>
}
