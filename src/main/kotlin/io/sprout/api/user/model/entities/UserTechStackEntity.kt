package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.selection.model.entities.TechStackEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_tech_stack")
class UserTechStackEntity(

    @ManyToOne
    var techStackEntity: TechStackEntity,

    @ManyToOne
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}