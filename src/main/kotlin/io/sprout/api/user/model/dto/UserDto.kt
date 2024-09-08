package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.RoleType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class UserDto {

    @Schema(description = "유저 생성 request")
    data class CreateUserRequest(

        @Schema(description = "유저 명", nullable = false)
        @field:NotNull val name: String,

        @Schema(description = "유저 이메일", nullable = false)
        @field:NotNull val email: String,

        @Schema(description = "유저 프로필 이미지 URL", nullable = false)
        val avatarImgUrl: String? = null,

        @Schema(description = "유저 권한", nullable = false)
        @field:NotNull val role: RoleType,

        @Schema(description = "개인정보 취급 방침 동의 여부", nullable = false)
        @field:NotNull val marketingConsent: Boolean

    )

}
