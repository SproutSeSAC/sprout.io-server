package io.sprout.api.specification.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserDomainEntity
import jakarta.persistence.*

@Entity
@Table(name = "domain")
class DomainEntity(

    @Enumerated(EnumType.STRING)
    @Column(name = "domain_type", nullable = false, length = 20)
    var domainType: DomainType, // 도메인 타입

    @Column(name = "is_active")
    var isActive: Boolean // 활성화 여부

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

}

enum class DomainType {
    SOCIAL, MOBILITY, FASHION, FOOD_TECH, COMMERCE, HEALTH, FIN_TECH, PROP_TECH, AI, EDUCATION, WEB3, GAME, SECURITY
}