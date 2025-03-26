package io.sprout.api.campus.service

import io.sprout.api.campus.infra.CampusRepository
import io.sprout.api.campus.model.dto.CampusDto
import org.springframework.stereotype.Service

@Service
class CampusService(
    private val campusRepository: CampusRepository
) {
    fun getCampusList(): CampusDto.CampusListResponse {
        val campusList = campusRepository.findAll().sortedBy { it.id }
        val response = campusList.map { campus ->
            CampusDto.CampusListResponse.CampusDetail(
                id = campus.id,
                name = campus.name,
                longitude = campus.longitude,
                latitude = campus.latitude,
                naverPlaceId = campus.naverPlaceId
            )
        }

        return CampusDto.CampusListResponse(
            campusList = response
        )
    }


}