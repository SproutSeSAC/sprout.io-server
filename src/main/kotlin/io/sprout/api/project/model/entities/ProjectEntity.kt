package io.sprout.api.project.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.project.model.enum.ContactMethod
import io.sprout.api.project.model.enum.MeetingType
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "project")
class ProjectEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val recruitmentCount: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val contactMethod: ContactMethod,

    @Column(nullable = false)
    val recruitmentStart: LocalDate,

    @Column(nullable = false)
    val recruitmentEnd: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val meetingType: MeetingType,

    @OneToMany(mappedBy = "project" ,  cascade = [CascadeType.ALL], )
    val projectParticipations: List<ProjectParticipationEntity> = listOf(),

    @OneToMany(mappedBy = "project",  cascade = [CascadeType.ALL], )
    val positions: List<ProjectPositionEntity> = listOf(),

    @OneToMany(mappedBy = "project",  cascade = [CascadeType.ALL], )
    val techStacks: List<ProjectTechStackEntity> = listOf()
) : BaseEntity()