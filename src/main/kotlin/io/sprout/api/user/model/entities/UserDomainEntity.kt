package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_domain")
class UserDomainEntity(

    @Enumerated(EnumType.STRING)
    @Column(name = "domain_type", nullable = false, length = 20)
    var domainType: DomainType, // 관심 도메인 타입

    @ManyToOne
    var user: UserEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}

enum class DomainType {
    SOCIAL, MOBILITY, FASHION, FOOD_TECH, COMMERCE, HEALTH, FIN_TECH, PROP_TECH, AI, EDUCATION, WEB3, GAME, SECURITY
}