package io.sprout.api.notice.model.controller

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.service.NoticeService
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
    fun getNotices(@RequestParam(required = false) noticeType: NoticeType?): List<NoticeResponseDto> {
        return noticeService.getNotices(noticeType)
    }

    // 특정 공지사항 조회
    @GetMapping("/{id}")
    fun getNoticeById(@PathVariable id: Long): NoticeResponseDto {
        return noticeService.getNoticeById(id)
    }
}