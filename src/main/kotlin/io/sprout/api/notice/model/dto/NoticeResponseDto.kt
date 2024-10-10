package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import java.time.LocalDateTime

data class NoticeResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val status: NoticeStatus,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime?
){
    companion object {
        fun fromEntity(notice: NoticeEntity): NoticeResponseDto {
            return NoticeResponseDto(
                id = notice.id,
                title = notice.title,
                content = notice.content,
                writerName = notice.writer.name!!,
                startDate = notice.startDate,
                endDate = notice.endDate,
                status = notice.status ?: NoticeStatus.ACTIVE,
                noticeType = notice.noticeType,
                createdDateTime = notice.createdAt,
                modifiedDateTime = notice.updatedAt
            )
        }
    }
}