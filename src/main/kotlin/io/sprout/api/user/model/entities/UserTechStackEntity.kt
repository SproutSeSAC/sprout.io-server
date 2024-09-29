package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.specification.model.entities.TechStackEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_tech_stack")
class UserTechStackEntity(

    @ManyToOne
    @JoinColumn(name = "tech_stack_id")
    var techStack: TechStackEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}