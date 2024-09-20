package io.sprout.api.specification.model.dto

import io.swagger.v3.oas.annotations.media.Schema

class SpecificationDto {

    @Schema(description = "직군 조회 dto")
    data class JobInfoDto(
        @Schema(description = "직군 Id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val job: String
    )

    @Schema(description = "도메인 조회 dto")
    data class DomainInfoDto(
        @Schema(description = "도메인 Id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val domain: String
    )

    @Schema(description = "기술스택 조회 dto")
    data class TechStackInfoDto(
        @Schema(description = "기술스택 Id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val techStack: String
    )
}