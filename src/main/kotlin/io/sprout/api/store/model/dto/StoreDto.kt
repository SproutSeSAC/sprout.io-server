package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.FoodType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class StoreDto {

    data class StoreListRequest(
        @Schema(description = "캠퍼스 아이디", nullable = false)
        val campusId: Long,

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
            val storeImage: String?,

            @Schema(description = "맛집 주소")
            val address: String,

            @Schema(description = "캠퍼스 명")
            val campusName: String,

            @Schema(description = "브레이크 시간")
            val breakTime: String,

            @Schema(description = "영업일 및 영업 시간")
            val workingDay: String,

            @Schema(description = "전화 번호")
            val contact: String,

            @Schema(description = "tag 리스트")
            val tagList: List<String>,

            @Schema(description = "요리 타입", example = "KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE")
            val foodType: FoodType,

            @Schema(description = "도보 시간 - 5분 이내 여부")
            val walkTime: Int,

            @Schema(description = "맛집 네이버 ID")
            val mapSchemaUrl: String?
        )
    }

    @Schema(description = "맛집 리스트 조회 response")
    data class StoreFilterResponse(
        @Schema(description = "제로페이 카운트")
        val zeropayCount: Int,

        @Schema(description = "만원 이하 메뉴 키운트")
        val underPriceCount: Int,

        @Schema(description = "5인 이상 가능 여부 카운트")
        val overPersonCount: Int,

        @Schema(description = "도보 시간 5분 이내 카운트")
        val walkTimeCount: Int,

        val koreanFoodCount: Int,

        val chineseFoodCount: Int,

        val japanesesFoodCount: Int,

        val westernFoodCount: Int,

        val asianFoodCount: Int,

        val snackCount: Int,

        val cafeCount: Int
    )

    @Schema(description = "맛집 상세 조회 response")
    data class StoreDetailResponse(
        @Schema(description = "맛집 명")
        val name: String,

        @Schema(description = "맛집 대표 이미지")
        val storeImageList: List<String>,

        @Schema(description = "맛집 주소")
        val address: String,

        @Schema(description = "캠퍼스 명")
        val campusName: String,

        @Schema(description = "브레이크 시간")
        val breakTime: String,

        @Schema(description = "영업일 및 영업 시간")
        val workingDay: String,

        @Schema(description = "전화 번호")
        val contact: String,

        @Schema(description = "요리 타입", example = "KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE")
        val foodType: FoodType,

        @Schema(description = "도보 시간")
        val walkTime: Int,

        @Schema(description = "tag 리스트")
        val tagList: List<String>,

        @Schema(description = "메뉴 리스트")
        val storeMenuList: MutableSet<StoreMenuDetail> = LinkedHashSet(),

        @Schema(description = "맛집 리뷰 리스트")
        val storeReviewList: MutableSet<StoreReviewDetail> = LinkedHashSet()

    ) {

        data class StoreMenuDetail(
            val id: Long,
            val name: String,
            val price: Int,
            val imageUrl: String?
        )

        data class StoreReviewDetail(
            val nickname: String,
            val review: String?,
            val profileImageUrl: String?,
            val rating: Int?,
            val createdAt: LocalDateTime
        )
    }

}
