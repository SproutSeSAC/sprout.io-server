package io.sprout.api.notice.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult
import io.sprout.api.notice.repository.NoticeParticipantRepository
import io.sprout.api.notice.repository.NoticeRepository
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
    private val noticeParticipantRepository: NoticeParticipantRepository,
    private val securityManager: SecurityManager,
    private val eventPublisher: ApplicationEventPublisher,
    private val userRepository: UserRepository
) : NoticeService {

    /**
     * 일반 공지사항 생성
     * (일반공지, 취업, 기타 타입)
     *
     * @param normalNoticeRequest 일반 공지사항 생성 파라미터
     *
     * @return noticeId
     */
    @Transactional
    override fun createNormalNotice(normalNoticeRequest: NormalNoticeRequestDto): Long {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        val noticeEntity = normalNoticeRequest.toEntity(userId)
        noticeRepository.save(noticeEntity)

        return noticeEntity.id
    }

    /**
     * 강의가 있는 세션 공지사항 생성
     * (특별강의, 이벤트 타입)
     *
     * @param sessionNoticeRequest 세션 공지사항 생성 파라미터
     *
     * @return noticeId
     */
    override fun createSessionNotice(sessionNoticeRequest: SessionNoticeRequestDto): Long {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        val noticeEntity = sessionNoticeRequest.toEntity(userId)
        noticeRepository.save(noticeEntity)

        return noticeEntity.id
    }




    @Transactional
    override fun updateNotice(id: Long, dto: NormalNoticeRequestDto): NoticeResponseDto {
        TODO("Not yet implemented")

//        val notice = noticeRepository.findById(id)
//            .orElseThrow { IllegalArgumentException("Notice with ID $id not found") }
//
//        notice.title = dto.title
//        notice.content = dto.content
//        notice.startDate = dto.urls.first().startDate
//        notice.endDate = dto.urls.first().endDate
//        notice.noticeType = dto.noticeType
//
//        val updatedNotice = noticeRepository.save(notice)
//        return updatedNotice.toDto()
    }


    @Transactional(readOnly = true)
    override fun getNoticeById(id: Long): NoticeResponseDto {
        TODO("Not yet implemented")
//        val notice = noticeRepository.findById(id)
//            .orElseThrow { IllegalArgumentException("Notice with ID $id not found") }
//        return notice.toDto()
    }


    @Transactional
    override fun deleteNotice(id: Long) {
        TODO("Not yet implemented")
//        if (!noticeRepository.existsById(id)) {
//            throw IllegalArgumentException("Notice with ID $id does not exist")
//        }
//        noticeRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun getFilterNotice(filter: NoticeFilterRequest): Pair<List<NoticeResponseDto>, Long> {
        TODO("Not yet implemented")
//
//        return noticeRepository.filterNotices(filter, securityManager.getAuthenticatedUserName()!!)
    }

    @Transactional
    override fun requestJoinNotice(noticeId: Long): RequestResult {
        TODO("Not yet implemented")
//        val userId = securityManager.getAuthenticatedUserName()
//        val requestUser = userRepository.findUserById(userId!!)
//        val notice = noticeRepository.findById(noticeId).orElseThrow {
//            IllegalArgumentException("Notice with ID $noticeId not found")
//        }
//
//         이미 공지에 참여한 사용자인지 확인
//        if (noticeParticipantRepository.findByUserAndNotice(requestUser!!, notice) != null) {
//            return RequestResult.ALREADY_PARTICIPATED // 이미 참여한 경우
//        }
//
//         이미 참여 요청을 한 경우 확인
//        if (noticeJoinRequestRepository.findByUserAndNotice(requestUser, notice) != null) {
//            return RequestResult.ALREADY_REQUESTED // 이미 요청한 경우
//        }
//
//        return try {
//            noticeJoinRequestRepository.save(NoticeJoinRequestEntity(0, requestUser, notice))
//            publishParticipationRequestEvent(notice, requestUser)
//            RequestResult.SUCCESS // 요청 성공
//        } catch (e: Exception) {
//            RequestResult.ERROR // 기타 오류 발생
//        }
    }

    @Transactional
    override fun acceptRequest(noticeId: Long, requestId: Long): AcceptRequestResult {
        TODO("Not yet implemented")

//        val notice = noticeRepository.findById(noticeId).orElseThrow {
//            IllegalArgumentException("Notice with ID $id not found")
//        }
//         참여 요청 확인
//        val joinRequest = noticeJoinRequestRepository.findById(requestId).orElse(null)
//            ?: return AcceptRequestResult.REQUEST_NOT_FOUND // 요청이 이미 취소된 경우
//
//        noticeJoinRequestRepository.delete(joinRequest)
//
//         정원 초과 확인
//        if (notice.participantCount >= notice.participantCapacity) {
//            return AcceptRequestResult.CAPACITY_EXCEEDED
//        }
//
//         참가자 정보 저장
//        notice.participantCount++
//
//
//        return try {
//            noticeRepository.save(notice) // 버전 충돌 발생 시 예외
//            val requestUser = UserEntity(joinRequest.user.id)
//            noticeParticipantRepository.save(NoticeParticipantEntity(0, requestUser, notice))
//            publishParticipationResponseEvent(notice, requestUser, accepted = true)
//            AcceptRequestResult.SUCCESS
//        } catch (e: OptimisticLockException) {
//            AcceptRequestResult.VERSION_CONFLICT
//        }
    }

    @Transactional
    override fun rejectRequest(noticeId: Long, requestId: Long): Boolean {
        TODO("Not yet implemented")

//        // 해당 공지사항에 대한 사용자의 참여 요청 확인
//        val joinRequest = noticeJoinRequestRepository.findById(requestId).orElse(null)
//            ?: return false // 요청이 없는 경우 false 반환
//
//        // 참여 요청 거절 로직 - 요청 삭제
//        noticeJoinRequestRepository.delete(joinRequest)
//        publishParticipationResponseEvent(joinRequest.notice, joinRequest.user, accepted = false)
//        return true
    }

    override fun getRequestList(noticeId: Long): List<NoticeJoinRequestListDto> {
        TODO("Not yet implemented")
//        return noticeJoinRequestRepository.getRequestList(noticeId)
    }


    // 공통 이벤트 발생 메서드
    private fun publishParticipationRequestEvent(notice: NoticeEntity, user: UserEntity) {
        TODO("Not yet implemented")
//        val event = ParticipationRequestEvent(
//            noticeId = notice.id,
//            userId = user.id,
//            userName =       user.name ?: "이름 없음",
//            userNickName = user.nickname,
//            noticeTitle = notice.title
//        )
//        eventPublisher.publishEvent(event)
    }

    private fun publishParticipationResponseEvent(notice: NoticeEntity, user: UserEntity, accepted: Boolean) {
        TODO("Not yet implemented")
//        val event = ParticipationResponseEvent(
//            noticeId = notice.id,
//            userId = user.id,
//            userName = user.name ?: "이름 없음",
//            userNickName = user.nickname,
//            noticeTitle = notice.title,
//            accepted = accepted
//        )
//        eventPublisher.publishEvent(event)
    }
}