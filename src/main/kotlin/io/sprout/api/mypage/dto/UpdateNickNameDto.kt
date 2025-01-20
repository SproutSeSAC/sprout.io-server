package io.sprout.api.mypage.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "닉네임 수정 요청")
data class UpdateNickNameDto(
        @Schema(description = "변경 할 닉네임", example = "new")
        val nickname: String
) {
}