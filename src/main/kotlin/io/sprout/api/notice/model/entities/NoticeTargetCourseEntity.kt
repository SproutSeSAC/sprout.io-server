package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import jakarta.persistence.*

/**
 * 공지사항 대상 교육과정 엔티티
 */
@Entity
@Table(name = "notice_target_course")
class NoticeTargetCourseEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice : NoticeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course : CourseEntity

): BaseEntity()