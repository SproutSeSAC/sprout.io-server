package io.sprout.api.store.model.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "길찾기 응답")
data class DirectionResponse(
    @Schema(description = "응답 코드(0 성공, 나머지 실패)")
    val code: Int
){
    @Schema(description = "경로 위,경도 ")
    var path: List<List<Double>>? = null
}