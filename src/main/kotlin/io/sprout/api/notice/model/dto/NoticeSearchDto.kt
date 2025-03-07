package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.RoleType
import org.jsoup.Jsoup
import java.time.LocalDateTime


/**
 * 공지사항 검색 응답 DTO
 */
data class NoticeSearchDto(
    val noticeId: Long,

    val userId: Long,
    val username: String,
    val roleType: RoleType,

    val title: String,
    var content: String,
    val isContentOverMaxLength: Boolean,
    val status: NoticeStatus,
    val viewCount: Int,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime,

    var isScraped: Boolean,
    val targetCourse: List<String>
) {
    var postId: Long? = null
}

data class NoticeSearchResponseDto(
    val notices: MutableList<NoticeSearchDto>,
){
    var isLastPage: Boolean = true

    fun addPageResult(searchRequest: NoticeSearchRequestDto){
        if (notices.size > searchRequest.size) {
            isLastPage = false
            notices.removeLast()
        }
    }

    // 미리보기용
    fun removeHtmlTags() {
        notices
            .forEach {
                var fixedContent: String = Jsoup.parse(it.content).text()

                val parsedCharacterStartIndex = fixedContent.lastIndexOf('&')
                val parsedCharacterLastIndex = fixedContent.lastIndexOf(';')
                if (parsedCharacterStartIndex != -1 && parsedCharacterStartIndex > parsedCharacterLastIndex) {
                    fixedContent = fixedContent.substring(0, parsedCharacterStartIndex)
                }

                if (fixedContent.endsWith("<")) {
                    fixedContent = fixedContent.substring(0, fixedContent.length-1)
            }
                if (fixedContent.endsWith("</")) {
                    fixedContent = fixedContent.substring(0, fixedContent.length-2)
                }

                it.content = fixedContent
            }
    }
}
