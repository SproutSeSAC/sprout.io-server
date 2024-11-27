package io.sprout.api.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "계정 수정 request")
data class UpdateUserRequest(
    @Schema(description = "프로필 사진 url 임시", nullable = true)
    val profileImageUrl: String?,

    @Schema(description = "닉네임", nullable = true)
    val nickname: String?,

    @Schema(description = "관심 도메인 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
    val updatedDomainIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "관심 직군 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
    val updatedJobIdList: MutableSet<Long> = mutableSetOf(),

    @Schema(description = "수정된 기술 스택 업데이트 ID 리스트 - 업데이트되는 내용만 보낼 것")
    val updatedTechStackIdList: MutableSet<Long> = mutableSetOf()
)