package io.sprout.api.notice.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.entities.NoticeType

interface NoticeService
{
    fun createNotice(dto: NoticeRequestDto): NoticeResponseDto
    fun updateNotice(id: Long, dto: NoticeRequestDto): NoticeResponseDto
    fun getNotices(noticeType: NoticeType?): List<NoticeResponseDto>
    fun getNoticeById(id: Long): NoticeResponseDto
    fun deleteNotice(id: Long)
}