package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.specification.model.entities.DomainEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_domain")
class UserDomainEntity(

    @ManyToOne
    @JoinColumn(name = "domain_id")
    var domain: DomainEntity,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}