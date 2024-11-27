package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeCommentEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeCommentRepository : JpaRepository<NoticeCommentEntity, Long> {
    @EntityGraph(attributePaths = ["user"])
    fun findByNoticeId(noticeId: Long, pageable: Pageable): List<NoticeCommentEntity>
}