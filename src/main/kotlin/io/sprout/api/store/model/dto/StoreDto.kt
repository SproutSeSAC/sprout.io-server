package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.FoodType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class StoreDto {

    data class StoreListRequest(
        @Schema(description = "제로페이 사용가능 유무", nullable = true)
        val isZeropay: Boolean,

        @Schema(description = "만원 이하 메뉴 여부", nullable = true)
        val underPrice: Boolean,

        @Schema(description = "5인 이상 가능 여부", nullable = true)
        val overFivePerson: Boolean,

        @Schema(description = "도보 시간 - 5분 이내 여부", nullable = true)
        val walkTimeWithinFiveMinutes: Boolean,

        @Schema(description = "요리 타입 - 복수 선택 가능", nullable = true, example = "KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE")
        val foodTypeList: MutableSet<FoodType> = LinkedHashSet(),

        val page: Int,
        val size: Int
    )

    @Schema(description = "맛집 리스트 조회 response")
    data class StoreListResponse(
        val storeList: List<StoreDetail>
    ) {
        data class StoreDetail(
            @Schema(description = "맛집 ID")
            val id: Long,

            @Schema(description = "맛집 명")
            val name: String,

            @Schema(description = "맛집 대표 이미지")
            val storeImage: String,

            @Schema(description = "영업일 및 영업 시간")
            val workingDay: String,

            @Schema(description = "브레이크 시간")
            val breakTime: String,

            @Schema(description = "도보 시간 - 5분 이내 여부")
            val walkTimeWithinFiveMinutes: Boolean,

            @Schema(description = "5인 이상 가능 여부")
            val overFivePerson: Boolean,

            @Schema(description = "만원 이하 메뉴 여부")
            val underPrice: Boolean
        )
    }


}
