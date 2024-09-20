package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.specification.model.entities.JobEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_job")
class UserJobEntity(

    @ManyToOne
    @JoinColumn(name = "job_id")
    var job: JobEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

}