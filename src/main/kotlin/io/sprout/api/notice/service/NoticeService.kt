package io.sprout.api.notice.service

import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto


interface NoticeService
{
    fun createNotice(dto: NoticeRequestDto): NoticeResponseDto
    fun updateNotice(id: Long, dto: NoticeRequestDto): NoticeResponseDto

    fun getNoticeById(id: Long): NoticeResponseDto
    fun deleteNotice(id: Long)
    fun getFilterNotice(filter: NoticeFilterRequest): Pair<List<NoticeResponseDto>, Long>
}