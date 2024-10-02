package io.sprout.api.specification.model.dto

import io.swagger.v3.oas.annotations.media.Schema

class SpecificationsDto {

    @Schema(description = "job 리스트 조회 response")
    data class JobListResponse(
        val jobList: List<JobInfoDto>
    )

    @Schema(description = "직군 조회 dto")
    data class JobInfoDto(
        @Schema(description = "직군 id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val job: String
    )

    @Schema(description = "domain 리스트 조회 response")
    data class DomainListResponse(
        val domainList: List<DomainInfoDto>
    )

    @Schema(description = "도메인 조회 dto")
    data class DomainInfoDto(
        @Schema(description = "도메인 id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val domain: String
    )

    @Schema(description = "techStack 리스트 조회 response")
    data class TechStackListResponse(
        val techStackList: List<TechStackInfoDto>
    )

    @Schema(description = "기술스택 조회 dto")
    data class TechStackInfoDto(
        @Schema(description = "기술스택 id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val techStack: String,

        @Schema(description = "기술스택 아이콘 url", nullable = false)
        val iconImageUrl: String,

        @Schema(description = "그룹화된 직무명", nullable = false)
        val jobName: String
    )
}