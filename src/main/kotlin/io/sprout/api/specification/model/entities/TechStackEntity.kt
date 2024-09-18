package io.sprout.api.specification.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserTechStackEntity
import jakarta.persistence.*

@Entity
@Table(name = "tech_stack")
class TechStackEntity(

    @Column(name = "tech_stack_type", nullable = false, length = 50)
    var techStackType: TechStackType, // 기술 스택 타입

    @Column(name = "is_active")
    var isActive: Boolean // 활성화 여부

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "tech_stack", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userTechStackList: MutableSet<UserTechStackEntity> = LinkedHashSet()
}

enum class TechStackType {

}