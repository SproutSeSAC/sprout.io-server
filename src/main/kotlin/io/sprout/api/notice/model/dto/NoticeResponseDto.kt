package io.sprout.api.notice.model.dto

import com.querydsl.core.annotations.QueryProjection
import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import java.time.LocalDate
import java.time.LocalDateTime

data class NoticeResponseDto  @QueryProjection constructor(
    val id: Long,
    val title: String,
    val content: String,
    val writerName: String,
    val profileUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val status: NoticeStatus,
    val noticeType: NoticeType,
    val createdDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime?,
    val participantCapacity: Int,
    val viewCount: Int,
    var isScraped: Boolean,
    val parentId: Long? = null,
    val children:  MutableList<NoticeUrlInfo> = mutableListOf()
)