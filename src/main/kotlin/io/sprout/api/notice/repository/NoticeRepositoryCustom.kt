package io.sprout.api.notice.repository

import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeResponseDto

interface NoticeRepositoryCustom {
    fun filterNotices(filterRequest: NoticeFilterRequest, userId: Long): Pair<List<NoticeResponseDto>, Long>
}