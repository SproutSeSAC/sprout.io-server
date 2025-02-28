package io.sprout.api.mypage.dto

import io.sprout.api.post.entities.PostType

data class writerDto (
    val name: String,
    val nickname: String,
    val profileImg: String
)

data class PostInfoDto(
    val title: String,
    val content: String,
    val postType: PostType
)

data class GetPostResponseDto (
    val id: Long,
    val writer: writerDto,
    val postId: Long,
    val title: String,
    val postType: PostType,
    val content: String,
    val pType: String = "" // 프로젝트, 스터디 구분용
)