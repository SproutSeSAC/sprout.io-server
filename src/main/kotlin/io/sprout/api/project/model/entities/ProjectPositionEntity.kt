package io.sprout.api.project.model.entities

import io.sprout.api.specification.model.entities.JobEntity
import jakarta.persistence.*

@Entity
@Table(name = "project_position")
class ProjectPositionEntity(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: ProjectEntity,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "position_id")
//    val position: PositionEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    var position: JobEntity,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}

