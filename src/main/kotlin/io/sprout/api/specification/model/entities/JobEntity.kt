package io.sprout.api.specification.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserJobEntity
import jakarta.persistence.*

@Entity
@Table(name = "job")
class JobEntity(

    @Column(name= "job_type", nullable = false, length = 30)
    var jobType: JobType, // 직군 타입

    @Column(name = "is_active")
    var isActive: Boolean // 활성화 여부

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "job", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userJobList: MutableSet<UserJobEntity> = LinkedHashSet()
}

enum class JobType {
    FRONTEND, BACKEND, PUBLISHER, DEV_OPS, DATA_ANALYST, DATA_ENGINEER, DATA_SCIENTIST,
    AI_ENGINEER, ANDROID, IOS, QA, EMBEDDED, PM_PO, SECURITY, GAME_DESIGNER, UNITY
}