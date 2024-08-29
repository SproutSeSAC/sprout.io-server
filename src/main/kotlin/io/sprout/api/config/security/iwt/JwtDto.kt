package io.sprout.api.config.security.iwt

import jakarta.validation.constraints.NotBlank

class JwtDto {

    data class Response(
        val grantType: String,
        val accessToken: String,
        val accessTokenExpiresIn: Long,
        val refreshToken: String,
        val refreshTokenExpiresIn: Long
    )

    data class JwtReissueRequest(
        @field:NotBlank val refreshToken: String
    )
}