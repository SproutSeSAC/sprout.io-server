package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.NoticeJoinRequestListDto

interface NoticeJoinRequestRepositoryCustom {
    fun getRequestList(noticeId: Long) :List<NoticeJoinRequestListDto>
}