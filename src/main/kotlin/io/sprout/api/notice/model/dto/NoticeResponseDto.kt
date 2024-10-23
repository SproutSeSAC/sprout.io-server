package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import java.time.LocalDate
import java.time.LocalDateTime

data class NoticeResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val profileUrl: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: NoticeStatus,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime?,
    val viewCount : Int,
    var isScraped : Boolean,
){
    companion object {
        fun fromEntity(notice: NoticeEntity): NoticeResponseDto {
            return NoticeResponseDto(
                id = notice.id,
                title = notice.title,
                content = notice.content,
                writerName = notice.writer.name?: "익명의 사용자",
                profileUrl = notice.writer.profileImageUrl ?: "",
                startDate = notice.startDate,
                endDate = notice.endDate?: LocalDate.now(),
                status = notice.status ?: NoticeStatus.ACTIVE,
                noticeType = notice.noticeType,
                createdDateTime = notice.createdAt,
                modifiedDateTime = notice.updatedAt,
                viewCount = notice.viewCount,
                isScraped = false,
            )
        }
    }
}