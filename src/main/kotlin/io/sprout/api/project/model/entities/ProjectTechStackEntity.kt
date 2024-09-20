package io.sprout.api.project.model.entities


import io.sprout.api.techStack.model.entities.TechStackEntity
import jakarta.persistence.*

@Entity
@Table(name = "project_tech_stack")
class ProjectTechStackEntity (

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    val project: ProjectEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_id")
    val techStack: TechStackEntity,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

}