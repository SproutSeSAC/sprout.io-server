package io.sprout.api.project.model.entities

import io.sprout.api.position.model.entities.PositionEntity
import jakarta.persistence.*

@Entity
@Table(name = "project_position")
class ProjectPositionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: ProjectEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    val position: PositionEntity,
) {
}

