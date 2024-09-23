package io.sprout.api.specification.controller

import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.specification.service.SpecificationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/specifications")
class SpecificationsController(
    private val specificationsService: SpecificationsService
) {

    @GetMapping("/jobList")
    fun getJobList(): SpecificationsDto.JobListResponse {
        return specificationsService.getJobList()
    }

    @GetMapping("/domainList")
    fun getDomainList(): SpecificationsDto.DomainListResponse {
        return specificationsService.getDomainList()
    }

    @GetMapping("/techStackList")
    fun getTechStackList(): SpecificationsDto.TechStackListResponse {
        return specificationsService.getTechStackList()
    }

}