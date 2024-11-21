package io.sprout.api.mealPost.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

class MealPostDto {

    @Schema(description = "한끼팟 생성")
    data class MealPostCreateRequest(

        @Schema(description = "제목")
        val title: String? = "",

        @Schema(description = "모집 일자")
        @NotNull val appointmentTime: LocalDateTime,

        @Schema(description = "모집 위치")
        @NotNull val meetingPlace: String,

        @Schema(description = "참가 인원")
        @NotNull val memberCount: Int,

        @Schema(description = "맛집 명")
        @NotNull val storeName: String,

    )

    @Schema(description = "한끼팟 참여")
    data class ParticipationRequest(

        @Schema(description = "한끼팟 ID")
        @NotNull val mealPostId: Long
    )

    @Schema(description = "한끼팟 탈퇴")
    data class LeaveRequest(

        @Schema(description = "한끼팟 ID")
        @NotNull val mealPostId: Long
    )

    @Schema(description = "한끼팟 상세 조회")
    data class MealPostDetailResponse(

        @Schema(description = "한끼팟 ID")
        val mealPostId: Long,

        @Schema(description = "제목")
        val title: String,

        @Schema(description = "모집 일자")
        val appointmentTime: LocalDateTime,

        @Schema(description = "모집 위치")
        val meetingPlace: String,

        @Schema(description = "참가 인원")
        val memberCount: Int,

        @Schema(description = "가게 명")
        val storeName: Long,


    )

    @Schema(description = "한끼팟 리스트 조회")
    data class MealPostListResponse(
        @Schema(description = "한끼팟 리스트")
        val mealPosts: List<MealPostProjection>
    )
}