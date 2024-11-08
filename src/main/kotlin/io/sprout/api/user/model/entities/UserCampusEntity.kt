package io.sprout.api.user.model.entities

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_campus")
class UserCampusEntity(

    @ManyToOne
    @JoinColumn(name = "campus_id")
    var campus: CampusEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}