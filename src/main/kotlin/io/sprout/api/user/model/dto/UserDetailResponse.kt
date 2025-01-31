package io.sprout.api.user.model.dto

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "계정 정보 조회 response")
data class UserDetailResponse(
    @Schema(description = "유저 ID", nullable = false)
    val userId: Long,

    @Schema(description = "이메일", nullable = false)
    val email: String,

    @Schema(description = "전화번호")
    val phoneNumber: String?,

    @Schema(description = "유저 명", nullable = true)
    val name: String?,

    @Schema(description = "닉네임", nullable = false)
    val nickname: String,

    @Schema(description = "프로필 사진 url", nullable = true)
    val profileImageUrl: String?,

    @Schema(description = "유저 권한", nullable = false)
    var role: RoleType,

    @Schema(description = "캠퍼스 리스트", nullable = false)
    val campusList: MutableSet<SpecificationsDto.CampusInfoDto>,

    @Schema(description = "코스 리스트", nullable = false)
    val courseList: MutableSet<CourseDetail>,

    @Schema(description = "관심 직군 리스트")
    val jobList: MutableSet<SpecificationsDto.JobInfoDto>,

    @Schema(description = "관심 도메인 리스트")
    val domainList: MutableSet<SpecificationsDto.DomainInfoDto>,

    @Schema(description = "기술 스택 리스트")
    val techStackList: MutableSet<SpecificationsDto.TechStackInfoDto>
) {
    data class CourseDetail(
        @Schema(description = "코스 ID")
        val courseId: Long,
        @Schema(description = "코스 명")
        val courseTitle: String,
        @Schema(description = "코스 시작일")
        val courseStartDate: LocalDate,
        @Schema(description = "코스 수료일")
        val courseEndDate: LocalDate
    )

    constructor(user: UserEntity, campusEntities: List<CampusEntity>) : this(
        userId = user.id,
        name = user.name,
        email = user.email,
        phoneNumber = user.phoneNumber,
        campusList = campusEntities.map {
            SpecificationsDto.CampusInfoDto(
                it.id,
                it.name
            )
        }.toMutableSet(),
        courseList = user.userCourseList.map {
            CourseDetail(
                courseId = it.course.id,
                courseTitle = it.course.title,
                courseStartDate = it.course.startDate,
                courseEndDate = it.course.endDate
            )
        }.toMutableSet(),

        nickname = user.nickname,
        role = user.role,
        profileImageUrl = user.profileImageUrl,

        jobList = user.userJobList.map {
            SpecificationsDto.JobInfoDto(
                id = it.job.id,
                job = it.job.name
            )
        }.toMutableSet(),
        domainList = user.userDomainList.map {
            SpecificationsDto.DomainInfoDto(
                id = it.domain.id,
                domain = it.domain.name
            )
        }.toMutableSet(),
        techStackList = user.userTechStackList.map {
            SpecificationsDto.TechStackInfoDto(
                id = it.techStack.id,
                techStack = it.techStack.name,
                iconImageUrl = it.techStack.path ?: "",
                jobName = it.techStack.jobName ?: ""
            )
        }.toMutableSet()
    )

}