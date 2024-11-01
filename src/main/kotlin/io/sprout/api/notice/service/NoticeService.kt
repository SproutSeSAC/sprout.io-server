package io.sprout.api.notice.service

import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeJoinRequestListDto
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult


interface NoticeService {
    fun createNotice(dto: NoticeRequestDto): NoticeResponseDto
    fun updateNotice(id: Long, dto: NoticeRequestDto): NoticeResponseDto
    fun getNoticeById(id: Long): NoticeResponseDto
    fun deleteNotice(id: Long)
    fun getFilterNotice(filter: NoticeFilterRequest): Pair<List<NoticeResponseDto>, Long>
    fun requestJoinNotice(noticeId : Long): RequestResult
    fun acceptRequest(noticeId: Long , requestId :Long): AcceptRequestResult
    fun rejectRequest(noticeId: Long, requestId :Long): Boolean
    fun getRequestList(noticeId: Long) : List<NoticeJoinRequestListDto>
}