package io.sprout.api.project.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.project.model.enum.ContactMethod
import io.sprout.api.project.model.enum.MeetingType
import io.sprout.api.project.model.enum.PType
import io.sprout.api.user.model.entities.UserEntity
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    val writer: UserEntity,

    @Column(nullable = false)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val pType : PType,

    @Column(nullable = false)
    val recruitmentCount: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method", nullable = false)
    val contactMethod: ContactMethod,

    @Column(nullable = false)
    val recruitmentStart: LocalDate,

    @Column(nullable = false)
    val recruitmentEnd: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type" ,nullable = false)
    val meetingType: MeetingType,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val projectParticipations: List<ProjectParticipationEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val positions: List<ProjectPositionEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val techStacks: List<ProjectTechStackEntity> = listOf()
) : BaseEntity()