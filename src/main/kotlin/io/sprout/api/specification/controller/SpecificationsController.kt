package io.sprout.api.specification.controller

import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.specification.service.SpecificationsService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/specifications")
class SpecificationsController(
    private val specificationsService: SpecificationsService
) {

    @GetMapping("/jobList")
    @Operation(summary = "직군 선택지 조회 API", description = "직무 선택지 조회 API")
    fun getJobList(): SpecificationsDto.JobListResponse {
        return specificationsService.getJobList()
    }

    @GetMapping("/domainList")
    @Operation(summary = "도메인 선택지 조회 API", description = "도메인 선택지 조회 API")
    fun getDomainList(): SpecificationsDto.DomainListResponse {
        return specificationsService.getDomainList()
    }

    @GetMapping("/techStackList")
    @Operation(summary = "기술 스택 선택지 조회 API", description = "기술 스택 선택지 조회 API")
    fun getTechStackList(): SpecificationsDto.TechStackListResponse {
        return specificationsService.getTechStackList()
    }

}