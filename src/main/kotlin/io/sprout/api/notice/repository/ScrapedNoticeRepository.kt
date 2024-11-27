package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.ScrapedNoticeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrapedNoticeRepository : JpaRepository<ScrapedNoticeEntity, Long> {
    fun findByNoticeIdAndUserId(noticeId: Long, userId: Long): ScrapedNoticeEntity?
}