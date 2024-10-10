package io.sprout.api.notice.model.repository

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeType

interface NoticeRepositoryCustom {
    fun findByNoticeType(noticeType: NoticeType?): List<NoticeEntity>
}