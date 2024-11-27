package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.NoticeDetailResponseDto

interface NoticeRepositoryCustom {

    fun findByIdWithSession(noticeId: Long, userId: Long): List<NoticeDetailResponseDto.Session>
}