package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<NoticeEntity, Long>, NoticeRepositoryCustom {
    @Query("SELECT n FROM NoticeEntity n JOIN FETCH n.writer")
    fun findByIdAll(): List<NoticeEntity>?
}