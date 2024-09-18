package io.sprout.api.techStack.model.entities

import io.sprout.api.techStack.model.dto.TechStackResponseDto
import jakarta.persistence.*

@Entity
@Table(name = "tech_stack")
class TechStackEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 50)
    var name: String, // 기술명

) {
    fun toDto() = TechStackResponseDto(
        id = this.id,
        name = this.name
    )
}