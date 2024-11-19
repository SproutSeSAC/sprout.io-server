package io.sprout.api.mypage.repository

import io.sprout.api.mypage.entity.DummyPostScrap
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyPostScrapRepository : JpaRepository<DummyPostScrap, Int> {
    fun findAllByUserId(userId: Int): List<DummyPostScrap>

    fun deleteBypostscrapidAndUserId(postscrapid: Int, userId: Int): Int
}
