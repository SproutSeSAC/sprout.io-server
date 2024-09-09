package io.sprout.api.techStack.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "tech_stack")
class TechStackEntity(

    @Column(nullable = false, length = 50)
    var name: String, // 기술명

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}