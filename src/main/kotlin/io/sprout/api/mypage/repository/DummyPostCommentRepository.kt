package io.sprout.api.mypage.repository

import io.sprout.api.mypage.entity.DummyPostComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyPostCommentRepository : JpaRepository<DummyPostComment, Int> {
    fun findAllByUserId(userId: Int): List<DummyPostComment>
}
