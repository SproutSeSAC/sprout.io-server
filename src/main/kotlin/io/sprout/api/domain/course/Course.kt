package io.sprout.api.domain.course

import io.sprout.api.domain.base.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "course")
class Course(

    @Column(nullable = false, length = 50)
    var title: String, // 코스명

    @Column(nullable = false)
    var startDate: LocalDateTime, // 코스 시작일

    @Column(nullable = false)
    var endDate: LocalDateTime, // 코스 수료일

): BaseEntity()  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}