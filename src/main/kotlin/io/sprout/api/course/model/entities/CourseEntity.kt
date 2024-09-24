package io.sprout.api.course.model.entities

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "course")

class CourseEntity(

    @Column(nullable = false, length = 50)
    var title: String, // 코스명

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDateTime, // 코스 시작일

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDateTime, // 코스 수료일

    @ManyToOne
    var campus: CampusEntity?

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "course", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userList: MutableSet<UserEntity> = LinkedHashSet()

}