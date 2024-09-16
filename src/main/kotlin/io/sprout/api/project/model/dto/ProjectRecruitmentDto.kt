package io.sprout.api.project.model.dto

import io.sprout.api.project.model.entities.ProjectEntity
import io.sprout.api.project.model.enum.ContactMethod
import io.sprout.api.project.model.enum.MeetingType
import io.sprout.api.project.model.enum.PType
import io.sprout.api.user.model.entities.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class ProjectRecruitmentRequestDTO(
    @Schema(
        description = "모집 구분 (프로젝트, 스터디) - 대소문자 상관없이 'project' 또는 'study' 값만 허용됩니다.",
        example = "Project"
    )
    val recruitmentType: String,  // 모집 구분 (프로젝트, 스터디)
    val startDate: LocalDate,     // 모집 시작일
    val endDate: LocalDate,       // 모집 종료일
    val positions: List<Long>,  // 모집 직무 (프론트엔드, 백엔드 등)
    val requiredStacks: List<Long>, // 필요 스택 (사용되는 기술 스택)
    val recruitmentCount: Int =0, // 모집 인원 (예: 인원 미정, 10명 이상 등)
    val meetingType: String,      // 모집 유형 (온라인, 오프라인, 혼합)
    val contactMethod: String,    // 연락 방법 (이메일, 전화 등)
    val projectTitle: String,     // 프로젝트 제목
    val projectDescription: String // 프로젝트 상세 설명
) {
    fun toEntity(userId: Long?): ProjectEntity {
        return ProjectEntity(
            title = this.projectTitle,
            pType = PType.valueOf(this.recruitmentType.uppercase()),
            writer = UserEntity(userId!!),
            description = this.projectDescription,
            recruitmentStart = this.startDate,
            recruitmentEnd = this.endDate,
            recruitmentCount = this.recruitmentCount,
            meetingType = MeetingType.valueOf(this.meetingType.uppercase()),
            contactMethod = ContactMethod.valueOf(this.contactMethod.uppercase()),
        )
    }
}