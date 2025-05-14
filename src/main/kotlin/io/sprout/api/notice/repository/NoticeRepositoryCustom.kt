package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.NoticeStatus
import org.springframework.data.domain.PageRequest

interface NoticeRepositoryCustom {

    fun findByIdWithSession(noticeId: Long, userId: Long): List<NoticeDetailResponseDto.Session>
    fun search(searchRequest: NoticeSearchRequestDto, userId: Long): MutableList<NoticeSearchDto>
    fun getApplicationCloseNotice(userId: Long, size: Long, days: Long): MutableList<NoticeCardDto>?
    fun getSessions(userId: Long, pageable: PageRequest, applicationStatus: NoticeStatus?, keyword: String?): NoticeSessionResponseDto
}