package io.sprout.api.campus.model.dto

import io.swagger.v3.oas.annotations.media.Schema

class CampusDto {

    @Schema(description = "campus 리스트 조회 response")
    data class CampusListResponse(
        val campusList: List<CampusDetail>
    ) {
        data class CampusDetail(
            val id: Long,
            val name: String,
        )
    }

}