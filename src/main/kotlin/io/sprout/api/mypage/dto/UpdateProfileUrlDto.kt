package io.sprout.api.mypage.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "프로필 사진 수정 요청")
data class UpdateProfileUrlDto(
        @Schema(description = "변경할 프로필 사진 URL", example = "s3경로")
        val profileUrl: String
) {

}
