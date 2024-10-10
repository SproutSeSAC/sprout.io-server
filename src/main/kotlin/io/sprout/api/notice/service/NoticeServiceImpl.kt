package io.sprout.api.notice.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.notice.model.repository.NoticeRepository
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeServiceImpl (
    private val noticeRepository: NoticeRepository,
    private val securityManager: SecurityManager
): NoticeService{

    @Transactional
    override fun createNotice(dto: NoticeRequestDto): NoticeResponseDto {
        val writer = UserEntity(securityManager.getAuthenticatedUserName()!!)


        val notice = dto.toEntity(writer)
        val savedNotice = noticeRepository.save(notice)

        return savedNotice.toDto()
    }

    @Transactional
    override fun updateNotice(id: Long, dto: NoticeRequestDto): NoticeResponseDto {
        val notice = noticeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Notice with ID $id not found") }

        notice.title = dto.title
        notice.content = dto.content
        notice.startDate = dto.startDate
        notice.endDate = dto.endDate
        notice.noticeType = dto.noticeType

        val updatedNotice = noticeRepository.save(notice)
        return updatedNotice.toDto()
    }

    @Transactional(readOnly = true)
    override fun getNotices(noticeType: NoticeType?): List<NoticeResponseDto> {
        val notice = noticeRepository.findByNoticeType(noticeType)
        return notice.let { it ->
            it.map { it.toDto() } }
    }

    @Transactional(readOnly = true)
    override fun getNoticeById(id: Long): NoticeResponseDto {
        val notice = noticeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Notice with ID $id not found") }
        return notice.toDto()
    }


    @Transactional
    override fun deleteNotice(id: Long) {
        if (!noticeRepository.existsById(id)) {
            throw IllegalArgumentException("Notice with ID $id does not exist")
        }
        noticeRepository.deleteById(id)
    }
}