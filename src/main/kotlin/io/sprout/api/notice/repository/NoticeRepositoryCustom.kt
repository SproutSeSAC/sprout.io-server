package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.NoticeCardDto
import io.sprout.api.notice.model.dto.NoticeDetailResponseDto
import io.sprout.api.notice.model.dto.NoticeSearchRequestDto
import io.sprout.api.notice.model.dto.NoticeSearchDto
import io.sprout.api.user.model.entities.UserEntity

interface NoticeRepositoryCustom {

    fun findByIdWithSession(noticeId: Long, userId: Long): List<NoticeDetailResponseDto.Session>
    fun search(searchRequest: NoticeSearchRequestDto, userId: Long): MutableList<NoticeSearchDto>
    fun findEndingTomorrowNotice(userId: Long): MutableList<NoticeCardDto>?
}