package io.sprout.api.mypage.dto

data class PostCommentDto(
        var commentId: Int,
        var userId: Int,
        var postId: Int
)