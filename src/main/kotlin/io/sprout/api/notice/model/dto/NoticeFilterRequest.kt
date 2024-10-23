package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeType

data class NoticeFilterRequest (
    val noticeType: NoticeType?,
    val page: Int = 1,
    val size: Int = 20,
    val sort: String = "latest",
    val onlyScraped: Boolean = false,
    val keyword: String?,
)