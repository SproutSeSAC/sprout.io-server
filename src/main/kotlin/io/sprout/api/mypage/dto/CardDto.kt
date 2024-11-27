package io.sprout.api.mypage.dto

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.user.model.entities.UserCampusEntity
import io.sprout.api.user.model.entities.UserCourseEntity
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

    @Schema(description = "반환되는 교육과정 데이터 목록")
    data class CourseMini(
            @Schema(description = "교육과정 ID")
            val id: Long,

            @Schema(description = "교육과정 이름")
            val name: String,
            )

    @Schema(description = "반환되는 캠퍼스 데이터 목록")
    data class CampusMini(
            @Schema(description = "캠퍼스 ID")
            val id: Long,

            @Schema(description = "캠퍼스 이름")
            val name: String,
    )

    @Schema(description = "교육 카드")
    data class StudyCard(

            @Schema(description = "이메일")
            val email: String,

            @Schema(description = "캠퍼스")
            val campus: CampusMini,

            @Schema(description = "과정")
            val course: CourseMini,

            )

    @Schema(description = "카드 정보 (프로필 + 교육)")
    data class UserCard(

            @Schema(description = "프로필 카드")
            val profile: ProfileCard,

            @Schema(description = "교육 카드")
            val study: StudyCard,
            )
}