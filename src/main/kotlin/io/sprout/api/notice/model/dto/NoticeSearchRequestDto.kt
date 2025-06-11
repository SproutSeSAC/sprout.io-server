package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.RoleType

/**
 * 검색 요청 필터 Request DTO
 * RoleType == null 이라면 전체 검색이다.
 */
data class NoticeSearchRequestDto(
    val noticeType: NoticeType?,
    val roleType: RoleType?,
    val keyword: String?,
    val onlyScraped: Boolean?,

    val page: Int = 0,
    val size: Int = 10
){
    val offset
        get() = page * size
}