package io.sprout.api.domain.auth

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class AuthDto {

    data class AuthenticationGoogleRequest(
        @Schema(description = "google 이메일")
        @field:NotBlank val email: String,

        @Schema(description = "google 인증 토큰")
        @field:NotBlank val tokenId: String,
    )

    data class AuthenticationGoogleCodeRequest(
        @Schema(description = "google 이메일")
        @field:NotBlank val email: String,

        @Schema(description = "google 인증 토큰")
        @field:NotBlank val code: String,
    )

}