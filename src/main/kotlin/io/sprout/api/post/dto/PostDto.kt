package io.sprout.api.post.dto

class PostDto {
    data class NoticeScraplist (
        val id: Long,
        val name: String
    )

    data class ProjectScraplist (
        val id: Long,
        val name: String
    )

    data class ScrapList(
        val id: Long,
        val notices: List<NoticeScraplist>,
        val projects: List<ProjectScraplist>
        )
}