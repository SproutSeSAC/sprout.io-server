package io.sprout.api.project.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.project.model.dto.ProjectDetailResponseDto
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
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
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val writer: UserEntity,

    @Column(columnDefinition = "TEXT", nullable = false)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var pType: PType,

    @Column(nullable = false)
    var recruitmentCount: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method", nullable = false)
    var contactMethod: ContactMethod,

    @Column(name = "contact_detail", nullable = false)
    var contactDetail : String,

    @Column(nullable = false)
    var recruitmentStart: LocalDate,

    @Column(nullable = false)
    var recruitmentEnd: LocalDate,

    @Column(nullable = false)
    var viewCount: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    var projectStatus: ProjectStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    var meetingType: MeetingType,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    val projectParticipations: List<ProjectParticipationEntity> = listOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var positions: MutableSet<ProjectPositionEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var techStacks: MutableSet<ProjectTechStackEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val comments: List<ProjectCommentEntity> = listOf()

) : BaseEntity() {

    // 프로젝트 정보를 업데이트하는 메서드
    fun updateFromDto(dto: ProjectRecruitmentRequestDto) {
        this.title = dto.projectTitle
        this.description = dto.projectDescription
        this.pType = PType.valueOf(dto.recruitmentType.uppercase())
        this.recruitmentCount = dto.recruitmentCount
        this.contactMethod = ContactMethod.valueOf(dto.contactMethod.uppercase())
        this.recruitmentStart = dto.startDate
        this.recruitmentEnd = dto.endDate
        this.meetingType = MeetingType.valueOf(dto.meetingType.uppercase())
        // 필요한 경우 추가 필드도 업데이트
    }

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
            contactDetail = this.contactDetail,
            imgUrl = this.writer.profileImageUrl,
        )
    }

    fun toggleStatus() {
        projectStatus = if (projectStatus == ProjectStatus.ACTIVE) {
            ProjectStatus.INACTIVE
        } else {
            ProjectStatus.ACTIVE
        }
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
    INACTIVE, // 마감
}