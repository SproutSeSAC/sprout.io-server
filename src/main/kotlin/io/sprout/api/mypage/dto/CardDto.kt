package io.sprout.api.mypage.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.Optional

class CardDto {

    @Schema(description = "캠퍼스 단일")
    data class CampusInfo(
        @Schema(description = "캠퍼스 id", nullable = false)
        val id: Long,

        @Schema(description = "이름", nullable = false)
        val campusName: String,
    )

    @Schema(description = "코스 리스트")
    data class CourseInfo(
        @Schema(description = "코스 ID", nullable = false)
        val id: Long,

        @Schema(description = "코스 명", nullable = false)
        val courseName: String,
    )

    @Schema(description = "프로필 카드")
    data class ProfileCard(

            @Schema(description = "이름")
            val name: String?,

            @Schema(description = "닉네임")
            val nickname: String,

            @Schema(description = "전화번호")
            val phoneNumber: String?,

            @Schema(description = "프로필 사진 URL")
            val profileUrl: String?,

            )

    @Schema(description = "교육 카드")
    data class StudyCard(

            @Schema(description = "이메일")
            val email: String,

            @Schema(description = "캠퍼스")
            val campus: List<CampusInfo>,

            @Schema(description = "과정")
            val course: Optional<CourseInfo>,
            )

    @Schema(description = "카드 정보 (프로필 + 교육)")
    data class UserCard(

            @Schema(description = "프로필 카드")
            val profile: ProfileCard,

            @Schema(description = "교육 카드")
            val study: StudyCard,
            )
}