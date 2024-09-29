package io.sprout.api.specification.service

import io.sprout.api.specification.model.dto.SpecificationsDto
import io.sprout.api.specification.repository.DomainRepository
import io.sprout.api.specification.repository.JobRepository
import io.sprout.api.specification.repository.TechStackRepository
import org.springframework.stereotype.Service

@Service
class SpecificationsService(
    private val jobRepository: JobRepository,
    private val domainRepository: DomainRepository,
    private val techStackRepository: TechStackRepository
) {

    fun getJobList(): SpecificationsDto.JobListResponse {
        val jobList = jobRepository.findAll()
        val response = jobList.map { job ->
            SpecificationsDto.JobInfoDto(
                id = job.id,
                job = job.name
            )
        }

        return SpecificationsDto.JobListResponse(
            jobList = response
        )
    }

    fun getDomainList(): SpecificationsDto.DomainListResponse {
        val domainList = domainRepository.findAll()
        val response = domainList.map { domain ->
            SpecificationsDto.DomainInfoDto(
                id = domain.id,
                domain = domain.name
            )
        }

        return SpecificationsDto.DomainListResponse(
            domainList = response
        )
    }

    fun getTechStackList(): SpecificationsDto.TechStackListResponse {
        val techStackList = techStackRepository.findAll()
        val response = techStackList.map { techStack ->
            SpecificationsDto.TechStackInfoDto(
                id = techStack.id,
                techStack = techStack.name,
                iconImageUrl = techStack.path ?: ""
            )
        }

        return SpecificationsDto.TechStackListResponse(
            techStackList = response
        )
    }


}