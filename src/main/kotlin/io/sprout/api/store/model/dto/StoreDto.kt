package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.FoodType
import io.swagger.v3.oas.annotations.media.Schema

class StoreDto {

    data class StoreListRequest(
        @Schema(description = "제로페이 사용가능 유무", nullable = false)
        val isZeropay: Boolean,

        @Schema(description = "만원 이하 메뉴 여부", nullable = false)
        val underPrice: Boolean,

        @Schema(description = "5인 이상 가능 여부", nullable = false)
        val overFivePerson: Boolean,

        @Schema(description = "도보 시간 - 5분 이내 여부", nullable = false)
        val walkTimeWithinFiveMinutes: Boolean,

        @Schema(description = "요리 타입 - 복수 선택 가능", nullable = false, example = "KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE")
        val foodTypeList: MutableSet<FoodType> = LinkedHashSet(),

        @Schema(description = "페이지 번호", nullable = false, defaultValue = "1")
        val page: Int,

        @Schema(description = "페이지 사이즈", nullable = false, defaultValue = "20")
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

    @Schema(description = "맛집 리스트 조회 response")
    data class StoreFilterResponse(
        @Schema(description = "제로페이 카운트")
        val zeropayCount: Int,

        @Schema(description = "만원 이하 메뉴 키운트")
        val underPriceCount: Int,

        @Schema(description = "5인 이상 가능 여부")
        val overPersonCount: Int,

        @Schema(description = "도보 시간 5분 이내 ")
        val walkTimeCount: Int,

        val koreanFoodCount: Int,

        val chineseFoodCount: Int,

        val japanesesFoodCount: Int,

        val westernFoodCount: Int,

        val asianFoodCount: Int,

        val snackCount: Int,
    )


}
