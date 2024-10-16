package io.sprout.api.notice.model.controller

import io.sprout.api.notice.model.dto.NoticeFilterRequest
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("notices")
class NoticeController(
    private val noticeService: NoticeService
) {
    // 공지사항 등록
    @PostMapping
    fun createNotice(@RequestBody dto: NoticeRequestDto): NoticeResponseDto {
        return noticeService.createNotice(dto)
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    fun updateNotice(@PathVariable id: Long, @RequestBody dto: NoticeRequestDto): NoticeResponseDto {
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
}