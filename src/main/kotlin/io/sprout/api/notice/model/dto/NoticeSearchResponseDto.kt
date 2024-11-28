package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.RoleType
import java.time.LocalDateTime


//글쓴이, 제목, 내용, 타입, 대상코스, 조회수, isScraped, 작성일자, 수정일자
// user 필요하고
// noticeCourse 필요하고
// scraped Notice 필요하고,
/**
 * 공지사항 검색 응답 DTO
 */
data class NoticeSearchResponseDto(
    val noticeId: Long,

    val userId: Long,
    val username: String,
    val roleType: RoleType,

    val title: String,
    val viewCount: Int,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime,

    var isScraped: Boolean,
    val targetCourse: List<String>
){
}
