package io.sprout.api.notice.controller

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.ParticipantStatus
import io.sprout.api.notice.service.NoticeService
import io.swagger.v3.oas.annotations.Operation
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
    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
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
    @Operation(summary = "공지사항 수정", description = "PUT 방식으로 공지사항 등록과 동일하게 보내면 됩니다.")
    fun updateNotice(
        @PathVariable noticeId: Long, @RequestBody noticeRequest: NoticeRequestDto): ResponseEntity<Map<String, Long>> {
        noticeService.updateNotice(noticeId, noticeRequest)

        return ResponseEntity.ok(mapOf("noticeId" to noticeId))
    }

    /**
     * 공지사항 상태 토글
     * 활성 <-> 비활성
     */
    @PatchMapping("/{noticeId}/status")
    @Operation(summary = "공지사항 상태 변경", description = "공지사항 상태 토글: 활성 <-> 비활성")
    fun toggleStatus(@PathVariable noticeId: Long): ResponseEntity<Any> {
        noticeService.toggleStatus(noticeId)

        return ResponseEntity.ok().build()
    }

    /**
     * 공지사항 조회
     *
     * @param noticeId 조회할 공지사항 ID
     */
    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 조회", description = "공지사항을 조회합니다. 댓글은 포함되어있지 않습니다.")
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
    @Operation(summary = "공지사항 검색", description = "공지사항을 검색합니다. keyword는 제목과 내용중 포함되는 부분을 찾습니다.")
    fun getNotices(
        @ModelAttribute searchRequest: NoticeSearchRequestDto
    ): ResponseEntity<NoticeSearchResponseDto> {
        val searchNotice = noticeService.searchNotice(searchRequest)

        return ResponseEntity.ok(searchNotice)
    }

    /**
     * 공지사항 마감 임박 API
     */
    @GetMapping("/ending-close")
    @Operation(summary = "마감 임박 공지사항 조회", description = "특강 세션 등록 마감이 가장 가까운 공지를 반환합니다. 기본값 7일")
    fun getApplicationCloseNotice(
        @RequestParam(defaultValue = "6") size: Long,
        @RequestParam(defaultValue = "7") days: Long
    ): ResponseEntity<MutableList<NoticeCardDto>> {
        val searchNotice = noticeService.getApplicationCloseNotice(size, days)

        return ResponseEntity.ok(searchNotice)
    }


    /**
     * 공지사항 댓글 조회
     *
     * @param noticeId 조회할 공지사항 ID
     * @param pageable 페이지네이션 요청 파라미터
     */
    @GetMapping("/{noticeId}/comments")
    @Operation(summary = "공지사항 댓글 조회", description = "공지사항의 댓글을 조회합니다. 무한스크롤 형태로 되어있습니다.")
    fun getNoticeComments(
        @PathVariable noticeId: Long,
        pageable: Pageable
    ): ResponseEntity<NoticeCommentResponseDto> {
        val noticeComments = noticeService.getNoticeComments(noticeId, pageable)

        return ResponseEntity.ok(noticeComments)
    }

    /**
     * 공지사항 댓글 작성
     *
     * @param noticeId 댓글 작성할 공지사항 ID
     * @param commentRequest 댓글 생성 요청 파라미터
     */
    @PostMapping("/{noticeId}/comments")
    @Operation(summary = "공지사항 댓글 작성", description = "댓글을 작성합니다.")
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
    @Operation(summary = "공지사항 댓글 삭제", description = "댓글을 삭제합니다.")
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
    @Operation(summary = "공지사항을 삭제합니다.", description = "연관된 모든 것을 삭제합니다.")
    fun deleteNotice(@PathVariable noticeId: Long) {
        noticeService.deleteNotice(noticeId)
    }

    /**
     * 공지사항 세션 참가 신청
     *
     * @param sessionId 공지사항 강의 세션 ID
     */
    @Operation(summary = "공지사항 세션에 참가 신청합니다.", description = "참가 대기 상태로 신청하게 됩니다.")
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
    @Operation(summary = "공지사항 세션 참가 수락", description = "참가 대기상태 또는 거절상태를 참가상태로 바꿉니다.")
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
    @Operation(summary = "공지사항 세션 참가 거절", description = "대기상태 또는 참가상태를 거절상태로 바꿉니다.")
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
    @Operation(summary = "공지사항 세션 참가 취소", description = "공지사항 참가 상태를 삭제합니다. (DB 삭제)")
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
    @Operation(summary = "공지사항 세션 참가자 확인", description = "공지사항 세션 참가자를 확인합니다.")
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

    /**
     * 공지사항 좋아요 토글
     *
     * @param noticeId 공지사항 ID
     * @return 200 ok
     */
    @PostMapping("/{noticeId}/scrap")
    @Operation(summary = "공지사항 좋아요 토글", description = "ture -> false, false -> true toggle")
    fun toggleNoticeScrap(@PathVariable noticeId: Long): ResponseEntity<ToggleResponse> {
        val result = noticeService.toggleNoticeScrap(noticeId)

        return ResponseEntity.ok(result)
    }

}



