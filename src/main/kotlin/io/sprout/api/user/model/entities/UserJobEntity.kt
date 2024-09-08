package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_job")
class UserJobEntity(

    @Enumerated(EnumType.STRING)
    @Column(name= "job_type", nullable = false, length = 50)
    var jobType: JobType, // 관심 직군 타입

    @ManyToOne
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

}

enum class JobType {
    FRONTEND, BACKEND, PUBLISHER, DEV_OPS, DATA_ANALYST, DATA_ENGINEER, DATA_SCIENTIST,
    AI_ENGINEER, ANDROID, IOS, QA, EMBEDDED, PM_PO, SECURITY, GAME_DESIGNER, UNITY
}