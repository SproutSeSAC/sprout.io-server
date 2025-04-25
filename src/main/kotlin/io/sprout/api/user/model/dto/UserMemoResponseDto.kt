package io.sprout.api.user.model.dto

import io.sprout.api.user.model.entities.UserMemo

data class UserMemoResponseDto(
    val memoId: Long,
    val content: String,
){
    constructor(memo: UserMemo) : this(
        memo.id,
        memo.content
    )
}
