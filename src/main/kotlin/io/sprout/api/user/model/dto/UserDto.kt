package io.sprout.api.user.model.dto

import io.sprout.api.specification.model.dto.SpecificationDto
import io.sprout.api.techStack.model.dto.TechStackResponseDto
import io.sprout.api.user.model.entities.RoleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class UserDto {

    @Schema(description = "계정 정보 조회 response")
    data class GetUserResponse(
        @Schema(description = "유저 명", nullable = true)
        val name: String?,

        @Schema(description = "닉네임", nullable = false)
        val nickname: String,

        @Schema(description = "프로필 사진 url", nullable = true)
        val profileImageUrl: String?,

        @Schema(description = "관심 직군 리스트")
        val jobList: MutableSet<SpecificationDto.JobInfoDto>,

        @Schema(description = "관심 도메인 리스트")
        val domainList: MutableSet<SpecificationDto.DomainInfoDto>,

        @Schema(description = "기술 스택 리스트")
        val techStackList: MutableSet<SpecificationDto.TechStackInfoDto>
    )

    @Schema(description = "추가 정보 입력에 따른 계정 생성 request")
    data class CreateUserRequest(
        @Schema(description = "코스 ID", nullable = false)
        @field:NotNull val courseId: Long,

        @Schema(description = "유저 명", nullable = false)
        @field:NotNull val name: String,

        @Schema(description = "닉네임", nullable = false)
        @field:NotNull val nickname: String,

        @Schema(description = "유저 프로필 이미지 URL", nullable = false)
        val avatarImgUrl: String? = null,

        @Schema(description = "유저 타입", nullable = false, example = "ADMIN, TRAINEE, PRE_TRAINEE, CAMPUS_MANAGER, EDU_MANAGER, JOB_COORDINATOR" )
        @field:NotNull val role: RoleType,

        @Schema(description = "관심 도메인 리스트")
        val domainIdList: MutableSet<Long> = mutableSetOf(),

        @Schema(description = "관심 직군 리스트")
        val jobIdList: MutableSet<Long> = mutableSetOf(),

        @Schema(description = "개인정보 취급 방침 동의 여부", nullable = false)
        @field:NotNull val marketingConsent: Boolean
    )

    @Schema(description = "계정 탈퇴 request")
    data class DeleteUserRequest(
        @Schema(description = "유저 아이디", nullable = false)
        @field:NotNull val userId: Long
    )

    @Schema(description = "계정 수정 request")
    data class UpdateUserRequest(
        @Schema(description = "프로필 사진 url 임시", nullable = true)
        val profileImageUrl: String?,

        @Schema(description = "닉네임", nullable = true)
        val nickname: String?,

        @Schema(description = "관심 도메인 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
        val updatedDomainIdList: MutableSet<Long> = mutableSetOf(),

        @Schema(description = "관심 직군 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
        val updatedJobIdList: MutableSet<Long> = mutableSetOf(),

        @Schema(description = "수정된 기술 스택 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
        val updatedTechStackIdList: MutableSet<Long> = mutableSetOf()
    )


}
