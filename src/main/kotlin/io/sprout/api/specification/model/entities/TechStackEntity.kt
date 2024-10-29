package io.sprout.api.specification.model.entities

import io.sprout.api.user.model.entities.UserTechStackEntity
import jakarta.persistence.*

@Entity
@Table(name = "tech_stack")
class TechStackEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 50)
    var name: String, // 기술명

    @Column(name = "is_active")
    var isActive: Boolean? // 활성화 여부

) {

    @Column(nullable = true, length = 500)
    var path: String? = null // 아이콘 이미지 경로

    @Column(name = "job_name", nullable = true)
    var jobName: String? = null // 그룹화된 job 이름


}