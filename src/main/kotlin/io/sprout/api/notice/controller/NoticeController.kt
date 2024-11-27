package io.sprout.api.notice.controller

import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult
import io.sprout.api.notice.service.NoticeService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    /**
     * 일반 공지사항 등록 엔드포인트
     * (일반, 취업, 기타 타입)
     *
     * @param normalNoticeRequest 일반 공지사항 등록 요청 파라미터
     * @return noticeId
     */
    @PostMapping("/normal")
    fun createNotice(@RequestBody @Valid normalNoticeRequest: NormalNoticeRequestDto): ResponseEntity<Map<String, Long>> {
        val noticeId = noticeService.createNormalNotice(normalNoticeRequest)

        return ResponseEntity.ok(mapOf("noticeId" to noticeId))
    }

    /**
     * 세션이 있는 특강 공지사항 등록 엔드포인트
     * (특강, 이벤트 타입)
     *
     * @param sessionNoticeRequest 세션 공지사항 등록 요청 파라미터
     * @return noticeId
     */
    @PostMapping("/session")
    fun createNotice(@RequestBody @Valid sessionNoticeRequest: SessionNoticeRequestDto): ResponseEntity<Map<String, Long>> {
        val noticeId = noticeService.createSessionNotice(sessionNoticeRequest)

        return ResponseEntity.ok(mapOf("noticeId" to noticeId))
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    fun updateNotice(@PathVariable id: Long, @RequestBody dto: NormalNoticeRequestDto): NoticeResponseDto {
        return noticeService.updateNotice(id, dto)
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    fun deleteNotice(@PathVariable id: Long) {
        noticeService.deleteNotice(id)
    }

    // 모든 공지사항 조회
    @GetMapping
    fun getNotices(
        @RequestParam(required = false) noticeType: NoticeType?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "false") onlyScraped: Boolean,
        @RequestParam(defaultValue = "latest") sort: String
    ): ResponseEntity<Map<String, Any?>> {
        val filterRequest = NoticeFilterRequest(
            noticeType = noticeType,
            keyword = keyword,
            page = page,
            size = size,
            onlyScraped = onlyScraped,
            sort = sort
        )

        val (filteredProjects, totalCount) = noticeService.getFilterNotice(filterRequest)
        val totalPages = (totalCount + filterRequest.size - 1) / filterRequest.size
        val nextPage = if (filterRequest.page.toLong() != totalPages) filterRequest.page + 1 else null

        val responseBody = mapOf(
            "projects" to filteredProjects,
            "totalCount" to totalCount,
            "currentPage" to filterRequest.page,
            "pageSize" to filterRequest.size,
            "totalPages" to totalPages,
            "nextPage" to nextPage
        )
        return ResponseEntity.ok(responseBody)
    }

    // 특정 공지사항 조회
    @GetMapping("/{id}")
    fun getNoticeById(@PathVariable id: Long): NoticeResponseDto {
        return noticeService.getNoticeById(id)
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