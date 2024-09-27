package io.sprout.api.specification.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserDomainEntity
import jakarta.persistence.*

@Entity
@Table(name = "domain")
class DomainEntity(

    @Column(nullable = false, length = 20)
    var name: String, // 도메인 명

    @Column(name = "is_active")
    var isActive: Boolean // 활성화 여부

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "domain", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userDomainList: MutableSet<UserDomainEntity> = LinkedHashSet()

}