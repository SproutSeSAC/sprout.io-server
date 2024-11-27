package io.sprout.api.notice.service

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult
import org.springframework.data.domain.Pageable


interface NoticeService {
    fun createNotice(noticeRequest: NoticeRequestDto): Long
    fun updateNotice(noticeId: Long, noticeRequest: NoticeRequestDto)
    fun getNoticeById(noticeId: Long): NoticeDetailResponseDto
    fun getNoticeComments(noticeId: Long, pageable: Pageable): List<NoticeCommentResponseDto>
    fun createNoticeComment(commentRequest: NoticeCommentRequestDto, noticeId: Long)
    fun deleteNotice(id: Long)
    fun searchNotice(searchRequest: NoticeSearchRequestDto): List<NoticeSearchResponseDto>
    fun requestJoinNotice(noticeId : Long): RequestResult
    fun acceptRequest(noticeId: Long , requestId :Long): AcceptRequestResult
    fun rejectRequest(noticeId: Long, requestId :Long): Boolean
    fun getRequestList(noticeId: Long) : List<NoticeJoinRequestListDto>
}