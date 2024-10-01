package io.sprout.api.project.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.project.model.dto.ProjectDetailResponseDto
import io.sprout.api.project.model.dto.ProjectResponseDto
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

    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var pType: PType,

    @Column(nullable = false)
    val recruitmentCount: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method", nullable = false)
    val contactMethod: ContactMethod,

    @Column(nullable = false)
    val recruitmentStart: LocalDate,

    @Column(nullable = false)
    val recruitmentEnd: LocalDate,

    @Column(nullable = false)
    var viewCount: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    val projectStatus: ProjectStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    val meetingType: MeetingType,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val projectParticipations: List<ProjectParticipationEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val positions: List<ProjectPositionEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val techStacks: List<ProjectTechStackEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val comments: List<ProjectCommentEntity> = listOf() // 댓글 리스트
) : BaseEntity() {

    fun toDto(): ProjectDetailResponseDto {
        return ProjectDetailResponseDto(
            id = this.id,
            title = this.title,
            writerId = this.writer.id,
            writerNickName = this.writer.nickname,
            description = this.description,
            pType = this.pType,
            recruitmentCount = this.recruitmentCount,
            contactMethod = this.contactMethod,
            recruitmentStart = this.recruitmentStart,
            recruitmentEnd = this.recruitmentEnd,
            viewCount = this.viewCount,
            projectStatus = this.projectStatus,
            meetingType = this.meetingType,
            createdAt =  this.createdAt,
            positionNames = this.positions.map { it.position.name }, // Position의 name 필드로 가정
        )
    }
}

enum class PType {
    PROJECT,
    STUDY,
}

enum class MeetingType {
    ONLINE,
    OFFLINE,
    HYBRID  // 혼합형
}

enum class ContactMethod {
    EMAIL,
    PHONE,
    MESSENGER
}

enum class ProjectStatus {
    ACTIVE, // 모집 중
    INACTIVE, // 중지
    END // 마감
}