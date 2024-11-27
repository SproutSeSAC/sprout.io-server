package io.sprout.api.notice.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.ScrapedNoticeEntity
import io.sprout.api.notice.model.enum.AcceptRequestResult
import io.sprout.api.notice.model.enum.RequestResult
import io.sprout.api.notice.repository.NoticeCommentRepository
import io.sprout.api.notice.repository.NoticeParticipantRepository
import io.sprout.api.notice.repository.NoticeRepository
import io.sprout.api.notice.repository.ScrapedNoticeRepository
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeServiceImpl(
    private val noticeRepository: NoticeRepository,
    private val scrapedNoticeRepository: ScrapedNoticeRepository,
    private val noticeCommentRepository: NoticeCommentRepository,
    private val noticeParticipantRepository: NoticeParticipantRepository,
    private val securityManager: SecurityManager,
    private val eventPublisher: ApplicationEventPublisher,
    private val userRepository: UserRepository
) : NoticeService {

    /**
     * 공지사항 생성
     *
     * @param noticeRequest 공지사항 생성 파라미터
     * @return noticeId
     */
    override fun createNotice(noticeRequest: NoticeRequestDto): Long {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        val noticeEntity = noticeRequest.toEntity(userId)
        noticeRepository.save(noticeEntity)

        return noticeEntity.id
    }

    /**
     * 공지사항 수정
     * SET 자료구조의 equals overriding 방식으로 noticeSessions, targetCourses 수정 포함
     */
    override fun updateNotice(noticeId: Long, noticeRequest: NoticeRequestDto) {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        //권한 확인
        val noticeEntity = (noticeRepository.findByIdAndUserId(noticeId, userId)
            ?: throw CustomBadRequestException("게시글이 존재하지 않거나 권한이 없습니다."))
        noticeEntity.update(noticeRequest)

        noticeRepository.save(noticeEntity)
    }

    /**
     * 공지사항 조회
     *
     * @param noticeId 조회할 공지사항 ID
     * @return 공지사항 detail (comment 미포함)
     */
    @Transactional
    override fun getNoticeById(noticeId: Long): NoticeDetailResponseDto {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        val findNotice = noticeRepository.findByIdAndCoursesAndUser(noticeId)
            ?: throw CustomBadRequestException("Not found user")
        findNotice.increaseViewCount()

        val responseDto = NoticeDetailResponseDto(findNotice)

        responseDto.sessions = noticeRepository.findByIdWithSession(noticeId, userId)

        val isScraped: ScrapedNoticeEntity? = scrapedNoticeRepository.findByNoticeIdAndUserId(noticeId, userId)
        responseDto.isScraped = (isScraped != null)


        return responseDto
    }

    /**
     *  공지사항 댓글 조회
     *
     *  @param noticeId 공지사항 ID
     *  @param pageable 페이지네이션 요청 파라미터
     */
    override fun getNoticeComments(noticeId: Long, pageable: Pageable): List<NoticeCommentResponseDto> {
        val sortedPageable = PageRequest.of(
            if (pageable.pageNumber == 0) 0 else pageable.pageNumber - 1,
            pageable.pageSize,
            Sort.by("createdAt").descending())

        return noticeCommentRepository.findByNoticeId(noticeId, sortedPageable)
            .map { NoticeCommentResponseDto(it) }
    }

    /**
     * 공지사항 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     */
    override fun deleteNoticeComment(commentId: Long) {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        noticeCommentRepository.findByIdAndUserId(commentId, userId) ?:
            throw CustomBadRequestException("게시글이 존재하지 않거나 삭제 권한이 없습니다.")

        noticeCommentRepository.deleteById(commentId)
    }

    /**
     * 공지사항 댓글 생성
     * 
     * @param commentRequest 공지사항 댓글 생성 파라미터
     */
    override fun createNoticeComment(commentRequest: NoticeCommentRequestDto, noticeId: Long) {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        noticeCommentRepository.save(commentRequest.toEntity(userId, noticeId))
    }

    /**
     *  공지사항 검색
     *
     *  @param searchRequest 공지사항 검색 파라미터
     */
    override fun searchNotice(searchRequest: NoticeSearchRequestDto): List<NoticeSearchResponseDto> {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        return noticeRepository.search(searchRequest, userId)
    }


    @Transactional
    override fun deleteNotice(id: Long) {
        TODO("Not yet implemented")
//        if (!noticeRepository.existsById(id)) {
//            throw IllegalArgumentException("Notice with ID $id does not exist")
//        }
//        noticeRepository.deleteById(id)
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