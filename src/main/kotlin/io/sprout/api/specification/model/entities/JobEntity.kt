package io.sprout.api.specification.model.entities

import io.sprout.api.user.model.entities.UserJobEntity
import jakarta.persistence.*

fun jobEntityOf(jobId: Long): JobEntity {
    val jobEntity = JobEntity("temp", false)
    jobEntity.id = jobId

    return jobEntity
}

@Entity
@Table(name = "job")
class JobEntity(

    @Column(nullable = false, length = 30)
    var name: String, // 직군 명

    @Column(name = "is_active")
    var isActive: Boolean // 활성화 여부

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}