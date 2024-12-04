package io.sprout.api.notice.service

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.ParticipantStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable


interface NoticeService {
    fun createNotice(noticeRequest: NoticeRequestDto): Long
    fun updateNotice(noticeId: Long, noticeRequest: NoticeRequestDto)
    fun getNoticeById(noticeId: Long): NoticeDetailResponseDto
    fun getNoticeComments(noticeId: Long, pageable: Pageable): NoticeCommentResponseDto
    fun createNoticeComment(commentRequest: NoticeCommentRequestDto, noticeId: Long)
    fun deleteNoticeComment(commentId: Long)
    fun deleteNotice(noticeId: Long)
    fun searchNotice(searchRequest: NoticeSearchRequestDto): NoticeSearchResponseDto
    fun applyForNoticeSession(sessionId: Long, participantRequest: NoticeSessionParticipantRequestDto)
    fun acceptNoticeSessionApplication(sessionId: Long, participantId :Long)
    fun rejectNoticeSessionApplication(sessionId: Long, participantId :Long)
    fun cancelNoticeSessionParticipant(sessionId: Long, participantId: Long)
    fun getSessionParticipants(sessionId: Long, pageable: PageRequest, searchParticipantStatus: List<ParticipantStatus>): Page<NoticeParticipantResponseDto>
}