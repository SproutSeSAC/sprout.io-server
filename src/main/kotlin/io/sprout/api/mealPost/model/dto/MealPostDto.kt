package io.sprout.api.mealPost.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

class MealPostDto {

    @Schema(description = "한끼팟 생성")
    data class CreateMealPostRequest(

        @Schema(description = "제목")
        val title: String? = "",

        @Schema(description = "모집 일자")
        @NotNull val appointmentTime: LocalDateTime,

        @Schema(description = "모집 위치")
        @NotNull val meetingPlace: String,

        @Schema(description = "참가 인원")
        @NotNull val memberCount: Int,

        @Schema(description = "storeId")
        @NotNull val storeId: Long,

    )

    @Schema(description = "한끼팟 생성")
    data class DeleteMealPostRequest(

        @Schema(description = "한끼팟 ID")
        @NotNull val mealPostId: Long

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
}