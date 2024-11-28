package io.sprout.api.notice.controller

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.ParticipantStatus
import io.sprout.api.notice.service.NoticeService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    /**
     * 공지사항 등록 엔드포인트
     *
     * @param noticeRequest 공지사항 등록 요청 파라미터
     * @return noticeId
     */
    @PostMapping
    fun createNotice(@RequestBody @Valid noticeRequest: NoticeRequestDto): ResponseEntity<Map<String, Long>> {
        val noticeId = noticeService.createNotice(noticeRequest)

        return ResponseEntity.ok(mapOf("noticeId" to noticeId))
    }

    /**
     * 공지사항 수정
     *
     * @param noticeRequest 일반 공지사항 등록 요청 파라미터
     * @param noticeId 수정할 공지사항 ID
     */
    @PutMapping("/{noticeId}")
    fun updateNotice(
        @PathVariable noticeId: Long, @RequestBody noticeRequest: NoticeRequestDto): ResponseEntity<Map<String, Long>> {
        noticeService.updateNotice(noticeId, noticeRequest)

        return ResponseEntity.ok(mapOf("noticeId" to noticeId))
    }

    /**
     * 공지사항 조회
     *
     * @param noticeId 조회할 공지사항 ID
     */
    @GetMapping("/{noticeId}")
    fun getNoticeById(@PathVariable noticeId: Long): ResponseEntity<NoticeDetailResponseDto> {
        val response = noticeService.getNoticeById(noticeId)

        return ResponseEntity.ok(response)
    }


    /**
     * 공지사항 검색
     * !! 자신의 교육과정에 해당하는 공지사항만 조회 가능 !!
     *
     * @param searchRequest 공지사항 검색 파라미터
     */
    @GetMapping
    fun getNotices(
        @ModelAttribute searchRequest: NoticeSearchRequestDto
    ): ResponseEntity<Map<String, Any?>> {
        val searchNotice = noticeService.searchNotice(searchRequest)

        return ResponseEntity.ok(mapOf("notices" to searchNotice))
    }

    /**
     * 공지사항 댓글 조회
     *
     * @param noticeId 조회할 공지사항 ID
     * @param pageable 페이지네이션 요청 파라미터
     */
    @GetMapping("/{noticeId}/comments")
    fun getNoticeComments(
        @PathVariable noticeId: Long,
        pageable: Pageable
    ): ResponseEntity<Map<String, List<NoticeCommentResponseDto>>> {
        val noticeComments = noticeService.getNoticeComments(noticeId, pageable)

        return ResponseEntity.ok(mapOf("comments" to noticeComments))
    }

    /**
     * 공지사항 댓글 작성
     *
     * @param noticeId 댓글 작성할 공지사항 ID
     * @param commentRequest 댓글 생성 요청 파라미터
     */
    @PostMapping("/{noticeId}/comments")
    fun createNoticeComment(
        @PathVariable noticeId: Long,
        @RequestBody commentRequest: NoticeCommentRequestDto
    ): ResponseEntity<Any> {
        noticeService.createNoticeComment(commentRequest, noticeId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     */
    @DeleteMapping("/comments/{commentId}")
    fun deleteNoticeComment(
        @PathVariable commentId: Long
    ): ResponseEntity<Any> {
        noticeService.deleteNoticeComment(commentId)

        return ResponseEntity.ok().build()
    }


    /**
     * 공지사항 삭제
     *
     * @param noticeId 삭제할 공지사항 ID
     */
    @DeleteMapping("/{noticeId}")
    fun deleteNotice(@PathVariable noticeId: Long) {
        noticeService.deleteNotice(noticeId)
    }

    /**
     * 공지사항 세션 참가 신청
     *
     * @param sessionId 공지사항 강의 세션 ID
     */
    @PostMapping("/sessions/{sessionId}/application")
    fun applyForNoticeSession(@PathVariable sessionId: Long): ResponseEntity<Any> {
        noticeService.applyForNoticeSession(sessionId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 세션 참가 수락
     *
     * @param sessionId 강의 세션 아이디
     * @param participantId 강의 참가 요청 ID
     */
    @PostMapping("/sessions/{sessionId}/accept/{participantId}")
    fun acceptParticipationRequest(
        @PathVariable sessionId: Long,
        @PathVariable participantId: Long
    ): ResponseEntity<Any> {
        noticeService.acceptNoticeSessionApplication(sessionId, participantId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 세션 참가 거절
     *
     * @param sessionId 강의 세션 아이디
     * @param participantId 강의 참가 요청 ID
     */
    @PostMapping("/sessions/{sessionId}/reject/{participantId}")
    fun rejectParticipationRequest(
        @PathVariable sessionId: Long,
        @PathVariable participantId: Long
    ): ResponseEntity<Any> {
        noticeService.rejectNoticeSessionApplication(sessionId, participantId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 세션 참가 취소 (삭제)
     *
     * @param sessionId 강의 세션 아이디
     * @param participantId 강의 참가 요청 ID
     */
    @DeleteMapping("/sessions/{sessionId}/cancel/{participantId}")
    fun cancelParticipantRequest(
        @PathVariable sessionId: Long,
        @PathVariable participantId: Long
    ): ResponseEntity<Any> {
        noticeService.cancelNoticeSessionParticipant(sessionId, participantId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 세션 확인 (신청자, 참가자, ... 포함)
     */
    @GetMapping("/sessions/{sessionId}")
    fun getSessionDetail(
        @PathVariable sessionId: Long,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "WAIT, PARTICIPANT, REJECT") searchParticipantStatus: List<ParticipantStatus>
    ): ResponseEntity<Page<NoticeParticipantResponseDto>> {
        val pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending())
        val result = noticeService.getSessionParticipants(sessionId, pageable, searchParticipantStatus)

        return ResponseEntity.ok(result)
    }

}



