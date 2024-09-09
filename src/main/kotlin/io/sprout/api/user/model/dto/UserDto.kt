package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.DomainType
import io.sprout.api.user.model.entities.JobType
import io.sprout.api.user.model.entities.RoleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.apache.commons.lang3.mutable.Mutable

class UserDto {

    @Schema(description = "유저 생성 request")
    data class CreateUserRequest(
        @Schema(description = "캠퍼스 ID", nullable = false)
        val campusId: Long,

        @Schema(description = "코스 ID", nullable = false)
        @field:NotNull val courseId: Long,

        @Schema(description = "유저 명", nullable = false)
        @field:NotNull val name: String,

        @Schema(description = "닉네임", nullable = false)
        @field:NotNull val nickname: String,

        @Schema(description = "유저 이메일", nullable = false)
        @field:NotNull val email: String,

        @Schema(description = "유저 프로필 이미지 URL", nullable = false)
        val avatarImgUrl: String? = null,

        @Schema(description = "유저 타입", nullable = false)
        @field:NotNull val role: RoleType,

        @Schema(description = "관심 직군 리스트")
        val domainList: MutableSet<DomainType> = mutableSetOf(),

        @Schema(description = "관심 도메인 리스트")
        val jobList: MutableSet<JobType> = mutableSetOf(),

        @Schema(description = "개인정보 취급 방침 동의 여부", nullable = false)
        @field:NotNull val marketingConsent: Boolean
    )

    @Schema(description = "계정 탈퇴 request")
    data class DeleteUserRequest(
        @Schema(description = "유저 아이디", nullable = false)
        @field:NotNull val userId: Long
    )


}
