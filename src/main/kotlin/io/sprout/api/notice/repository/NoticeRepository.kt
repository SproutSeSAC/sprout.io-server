package io.sprout.api.notice.repository

import io.sprout.api.notice.model.entities.NoticeEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<NoticeEntity, Long>, NoticeRepositoryCustom {
    @Query("SELECT n FROM NoticeEntity n JOIN FETCH n.user")
    fun findByIdAll(): List<NoticeEntity>?

    fun findByIdAndUserId(noticeId: Long, userId: Long): NoticeEntity?

    @Query("SELECT notice " +
            "FROM NoticeEntity notice " +
            "LEFT JOIN FETCH notice.user user " +
            "LEFT JOIN FETCH notice.targetCourses tcourse " +
            "LEFT JOIN FETCH tcourse.course course " +
            "WHERE notice.id = :noticeId ")
    fun findByIdAndCoursesAndUser(noticeId: Long): NoticeEntity?
}