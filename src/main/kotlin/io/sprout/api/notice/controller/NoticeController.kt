package io.sprout.api.notice.controller

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult
import io.sprout.api.notice.service.NoticeService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
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

    @PostMapping("/{noticeId}/join")
    fun requestJoinNotice(@PathVariable noticeId: Long): ResponseEntity<String> {
        return when (val result = noticeService.requestJoinNotice(noticeId)) {
            RequestResult.SUCCESS -> ResponseEntity.ok("Participation request successful.")
            RequestResult.ALREADY_PARTICIPATED -> ResponseEntity.badRequest().body("User has already joined this notice.")
            RequestResult.ALREADY_REQUESTED -> ResponseEntity.badRequest().body("User has already requested to join this notice.")
            RequestResult.ERROR -> ResponseEntity.status(500).body("An error occurred while processing the participation request.")
        }
    }

    @PostMapping("/{noticeId}/accept/{requestId}")
    fun acceptParticipationRequest(
        @PathVariable noticeId: Long,
        @PathVariable requestId: Long
    ): ResponseEntity<String> {
        return when (noticeService.acceptRequest(noticeId,requestId)) {
            AcceptRequestResult.SUCCESS -> ResponseEntity.ok("Participation confirmed.")
            AcceptRequestResult.REQUEST_NOT_FOUND -> ResponseEntity.status(410).body("The participation request has already been canceled.")
            AcceptRequestResult.VERSION_CONFLICT -> ResponseEntity.status(409).body("Another user has already confirmed this request.")
            AcceptRequestResult.CAPACITY_EXCEEDED -> ResponseEntity.status(403).body("Participation limit exceeded.")
        }
    }

    @PostMapping("/{noticeId}/reject/{requestId}")
    fun rejectParticipationRequest(
        @PathVariable noticeId: Long,
        @PathVariable requestId: Long
    ): ResponseEntity<String> {
        return if (noticeService.rejectRequest(noticeId, requestId)) {
            ResponseEntity.ok("Participation request rejected.")
        } else {
            ResponseEntity.status(404).body("Participation request not found or already canceled.")
        }
    }

    @GetMapping("/{noticeId}/requests")
    fun getRequestList(@PathVariable noticeId: Long): List<NoticeJoinRequestListDto> {
        return noticeService.getRequestList(noticeId)
    }
}