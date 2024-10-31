package io.sprout.api.notice.model.dto

import com.querydsl.core.annotations.QueryProjection
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.UserEntity
import java.time.LocalDate


data class NoticeUrlInfo @QueryProjection constructor(
    val url: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val subtitle: String,
    val parentId: Long?
)

data class NoticeRequestDto(
    val title: String,
    val content: String,
    val noticeType: NoticeType,
    val participantCapacity: Int,
    val urls: List<NoticeUrlInfo> // URL과 관련된 날짜 정보를 포함한 리스트
) {
    fun toEntity(writer: UserEntity, urlInfo: NoticeUrlInfo, parentId: Long? = null): NoticeEntity {
        return NoticeEntity(
            id = 0L,
            title = this.title,
            content = this.content,
            writer = writer,
            startDate = urlInfo.startDate, // URL에 맞는 시작일
            endDate = urlInfo.endDate, // URL에 맞는 종료일
            status = NoticeStatus.ACTIVE,
            noticeType = this.noticeType,
            participantCapacity = this.participantCapacity,
            viewCount = 0,
            url = urlInfo.url, // 해당 URL 설정
            parentId = parentId,
            subtitle = urlInfo.subtitle,
            participantCount = 0,
        )
    }
}
