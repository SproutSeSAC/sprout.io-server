package io.sprout.api.notice.schedule

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.notice.model.entities.ParticipantStatus
import io.sprout.api.notice.repository.NoticeParticipantRepository
import io.sprout.api.notice.repository.NoticeRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

//@Component
@Service
class NoticeScheduler(
    val noticeParticipantRepository: NoticeParticipantRepository,
    val noticeRepository: NoticeRepository,
) {
    private val log = LoggerFactory.getLogger(NoticeScheduler::class.java)

    /**
     * 특강 세션이 닫힌다면
     * 참가자는 상태를 COMPLETE, 대기자는 REJECT로 상태를 바꾸어준다.
     *
     */
    @Scheduled(cron = "0 0,30 * * * *")
    @Transactional
    fun completeNoticeParticipant() {
        val now = LocalDateTime.now()

        noticeParticipantRepository.updateParticipantToCompleteAfter(now)
        noticeParticipantRepository.updateWaitToRejectAfter(now)

        log.info("complete participants status update")
    }


    /**
     * 공지사항 특강/세션의 참가신청기간이 지나면 자동으로 INACTIVE상태로 바꾼다.
     */
    @Scheduled(cron = "1,30 * * * * *")
    @Transactional
    fun closeNoticeWhenEndDateTimePassed() {
        val now = LocalDateTime.now()

        noticeRepository.closeNoticeWhenApplicationDateTimeEnd(now)

        log.info("complete notice status update")
    }

}