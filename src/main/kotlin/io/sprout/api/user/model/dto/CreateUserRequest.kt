package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "추가 정보 입력에 따른 계정 생성 request")
data class CreateUserRequest(
    @Schema(description = "코스 ID 리스트")
    val courseIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "캠퍼스 ID 리스트")
    val campusIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "유저 명", nullable = false)
    @field:NotNull val name: String,

    @Schema(description = "닉네임", nullable = false)
    @field:NotNull val nickname: String,

    @Schema(description = "전화번호", nullable = false)
    @field:NotNull val phoneNumber: String,

    @Schema(description = "유저 타입", nullable = false, example = "ADMIN, TRAINEE, PRE_TRAINEE, CAMPUS_MANAGER, EDU_MANAGER, JOB_COORDINATOR")
    @field:NotNull val role: RoleType,

    @Schema(description = "관심 도메인 ID 리스트")
    val domainIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "관심 직군 ID 리스트")
    val jobIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "기술 스택 ID 리스트")
    val techStackIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "개인정보 취급 방침 동의 여부", nullable = false)
    @field:NotNull val marketingConsent: Boolean

)