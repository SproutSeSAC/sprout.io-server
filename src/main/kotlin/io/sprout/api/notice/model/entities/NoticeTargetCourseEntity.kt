package io.sprout.api.notice.model.entities

import io.sprout.api.course.model.entities.CourseEntity
import jakarta.persistence.*
import java.util.*

/**
 * 공지사항 대상 교육과정 엔티티
 */
@Entity
@Table(name = "notice_target_course")
class NoticeTargetCourseEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    val notice : NoticeEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course : CourseEntity

){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoticeTargetCourseEntity) return false

        return course.id == other.course.id
                && notice.id == other.notice.id
    }

    override fun hashCode(): Int {
        return Objects.hash(notice.id, course.id)
    }
}