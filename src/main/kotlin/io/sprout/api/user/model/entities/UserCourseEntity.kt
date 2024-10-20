package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_course")
class UserCourseEntity(

    @ManyToOne
    @JoinColumn(name = "course_id")
    var course: CourseEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}