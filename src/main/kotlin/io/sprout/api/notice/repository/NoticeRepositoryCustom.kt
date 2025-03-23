package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.NoticeCardDto
import io.sprout.api.notice.model.dto.NoticeDetailResponseDto
import io.sprout.api.notice.model.dto.NoticeSearchRequestDto
import io.sprout.api.notice.model.dto.NoticeSearchDto

interface NoticeRepositoryCustom {

    fun findByIdWithSession(noticeId: Long, userId: Long): List<NoticeDetailResponseDto.Session>
    fun search(searchRequest: NoticeSearchRequestDto, userId: Long): MutableList<NoticeSearchDto>
    fun getApplicationCloseNotice(userId: Long, size: Long, days: Long): MutableList<NoticeCardDto>?
}