package io.sprout.api.mypage.dto

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.course.model.entities.CourseEntity
import io.swagger.v3.oas.annotations.media.Schema

class CardDto {
    @Schema(description = "프로필 카드")
    data class ProfileCard(

            @Schema(description = "이름")
            val name: String?,

            @Schema(description = "닉네임")
            val nickname: String,

            @Schema(description = "프로필 사진 URL")
            val profileUrl: String?,

            )

    @Schema(description = "교육 카드")
    data class StudyCard(

            @Schema(description = "이메일")
            val email: String,

            @Schema(description = "캠퍼스")
            val campus: CampusEntity,

            @Schema(description = "과정")
            val course: CourseEntity,

            )

    @Schema(description = "카드 정보 (프로필 + 교육)")
    data class UserCard(

            @Schema(description = "프로필 카드")
            val name: ProfileCard,

            @Schema(description = "교육 카드")
            val nickname: StudyCard,
            )
}