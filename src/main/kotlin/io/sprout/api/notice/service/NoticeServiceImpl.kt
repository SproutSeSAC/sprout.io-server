package io.sprout.api.notice.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.notice.model.dto.*
import io.sprout.api.notice.model.entities.*
import io.sprout.api.notice.repository.*
import io.sprout.api.notification.entity.NotificationDto
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.scrap.repository.ScrapRepository
import io.sprout.api.sse.service.SseService
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.UserRepository
import io.sprout.api.utils.AuthorizationUtils
import jakarta.persistence.EntityNotFoundException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
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
    private val noticeSessionRepository: NoticeSessionRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val userRepository: UserRepository,
    private val sseService: SseService,
    private val postRepository: PostRepository,
    private val scrapRepository: ScrapRepository
) : NoticeService {

    /**
     * 공지사항 생성
     * 공지사항은 type에 따라 크게 2가지로 나뉜다. 강의 세션이 있는 것과 아닌 것
     *
     * @param noticeRequest 공지사항 생성 파라미터
     * @return noticeId
     */
    override fun createNotice(noticeRequest: NoticeRequestDto): Long {
        val user = getUser()
        AuthorizationUtils.validateUserIsManagerRole(user)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(user, noticeRequest.targetCourseIdList)

        val noticeEntity = if (noticeRequest.addIsSessionNotice()) {
            noticeRequest.toSessionEntity(user.id)
        } else {
            noticeRequest.toNormalEntity(user.id)
        }
        noticeRepository.save(noticeEntity)

        return noticeEntity.id
    }

    /**
     * 공지사항 수정
     * SET 자료구조의 equals overriding 방식으로 noticeSessions, targetCourses 수정 포함
     */
    override fun updateNotice(noticeId: Long, noticeRequest: NoticeRequestDto) {
        val user = getUser()

        val noticeEntity = noticeRepository.findByIdAndCoursesAndUser(noticeId)
            ?: throw CustomBadRequestException("게시글이 존재하지 않습니다.")

        noticeRepository.findByIdAndUserId(noticeId, user.id) ?: throw CustomBadRequestException("not authentication User")

        noticeEntity.update(noticeRequest)

        noticeRepository.save(noticeEntity)
    }

    /**
     * 공지사항 조회
     * 공지사항 targetCourse에 자신이 속한 course가 있어야 조회 가능
     *
     * @param noticeId 조회할 공지사항 ID
     * @return 공지사항 detail (comment 미포함)
     */
    @Transactional
    override fun getNoticeById(noticeId: Long): NoticeDetailResponseDto {
        val user = getUser()

        val findNotice = noticeRepository.findByIdAndCoursesAndUser(noticeId)
            ?: throw CustomBadRequestException("Not found notice")
        findNotice.increaseViewCount()

        AuthorizationUtils.validateUserCourseContainAtLeastOneTargetCourses(user, findNotice.targetCourses.map { it.course.id }.toSet())

        val responseDto = NoticeDetailResponseDto(findNotice)

        responseDto.sessions = noticeRepository.findByIdWithSession(noticeId, user.id)

        val post = postRepository.findByLinkedIdAndPostType(noticeId, PostType.NOTICE)
            ?: throw CustomBadRequestException("게시글(Post)이 없습니다.")
        responseDto.postId = post.id
        responseDto.isScraped = ((scrapRepository.findByUserIdAndPostId(user.id, post.id)) != null)

        return responseDto
    }

    /**
     *  공지사항 댓글 조회
     *
     *  @param noticeId 공지사항 ID
     *  @param pageable 페이지네이션 요청 파라미터
     */
    override fun getNoticeComments(noticeId: Long, pageable: Pageable): NoticeCommentResponseDto {
        val sortedPageable = PageRequest.of(
            if (pageable.pageNumber == 0) 0 else pageable.pageNumber - 1,
            pageable.pageSize,
            Sort.by("createdAt").descending()
        )

        val comments = noticeCommentRepository.findByNoticeId(noticeId, sortedPageable)
            .map { NoticeCommentResponseDto.NoticeCommentDto(it) }

        return NoticeCommentResponseDto(comments)
    }

    /**
     * 공지사항 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     */
    override fun deleteNoticeComment(commentId: Long) {
        val userId = getUserId()

        noticeCommentRepository.findByIdAndUserId(commentId, userId)
            ?: throw CustomBadRequestException("게시글이 존재하지 않거나 삭제 권한이 없습니다.")

        noticeCommentRepository.deleteById(commentId)
    }

    /**
     * 공지사항 댓글 생성
     *
     * @param commentRequest 공지사항 댓글 생성 파라미터
     */
    override fun createNoticeComment(commentRequest: NoticeCommentRequestDto, noticeId: Long) {
        val userId = getUserId()

        noticeCommentRepository.save(commentRequest.toEntity(userId, noticeId))
    }

    /**
     *  공지사항 검색
     *  공지사항 targetCourse에 자신이 속한 course가 있어야 조회 가능
     *
     *  @param searchRequest 공지사항 검색 파라미터
     */
    override fun searchNotice(searchRequest: NoticeSearchRequestDto): NoticeSearchResponseDto {
        val userId = getUserId()

        val user = getUser()

        val searchResult = noticeRepository.search(searchRequest, userId)
        searchResult.forEach { dto ->
            val post = postRepository.findByLinkedIdAndPostType(dto.noticeId, PostType.NOTICE)
            dto.postId = post?.id
            if (post != null)
            {
                dto.isScraped = ((scrapRepository.findByUserIdAndPostId(user.id, post.id)) != null)
            }
        }

        val searchResponse = NoticeSearchResponseDto(searchResult)
        searchResponse.addPageResult(searchRequest)
        searchResponse.removeHtmlTags()

        return searchResponse
    }

    /**
     * 공지사항 삭제
     * 공지사항과 연결된 - 댓글, 세션, 세션참여자, 타겟 교육과정 모두 삭제된다.
     *
     * @param noticeId 삭제할 공지사항 ID
     */
    @Transactional
    override fun deleteNotice(noticeId: Long) {
        val user = getUser()

        val noticeEntity = noticeRepository.findByIdAndCoursesAndUser(noticeId)
            ?: throw CustomBadRequestException("게시글이 존재하지 않습니다.")

        AuthorizationUtils.validateUserIsManagerRole(user)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(user, noticeEntity.targetCourses.map { it.course.id }.toSet())

        noticeCommentRepository.deleteByNoticeId(noticeId)
        scrapedNoticeRepository.deleteByNoticeId(noticeId)
        noticeRepository.deleteById(noticeId)
    }

    /**
     * 공지사항 세션 신청 (대기 인원으로 시작)
     *
     * @param sessionId 공지사항 강의 세션 ID
     */
    @Transactional
    override fun applyForNoticeSession(sessionId: Long) {
        val user = getUser()
        val session = noticeSessionRepository.findById(sessionId)
            .orElseThrow { throw CustomBadRequestException("세션이 존재하지 않습니다.") }
        AuthorizationUtils.validateUserCourseContainAtLeastOneTargetCourses(user, session.notice.targetCourses.map { it.course.id }.toSet())

        if (noticeParticipantRepository.findByNoticeSessionIdAndUserId(sessionId, user.id) != null) {
            throw CustomDataIntegrityViolationException("중복된 요청입니다.")
        }

        val dtodata = NotificationDto(
            fromId = user.id,
            userId = session.notice.user.id,
            type = 4,
            url = "",
            content = session.notice.title,
            NotiType = 2,
            comment = "",
        )

        sseService.publish(dtodata)

        noticeParticipantRepository.save(
            NoticeParticipantEntity(
                status = ParticipantStatus.WAIT,
                user = UserEntity(user.id),
                noticeSession = NoticeSessionEntity(sessionId)
            )
        )
    }

    /**
     * 세션 참가 신청 수락
     *
     * @param sessionId 세션 ID
     * @param participantId 참가 신청 ID
     */
    @Transactional
    override fun acceptNoticeSessionApplication(sessionId: Long, participantId: Long) {
        val participant = noticeParticipantRepository.findByIdAndNoticeSessionId(participantId, sessionId)
            ?: throw CustomBadRequestException("참가요청이 존재하지 않습니다.")

        val noticeSession = noticeSessionRepository.findByIdWithLock(sessionId)
            ?: throw CustomBadRequestException("해당 세션이 없습니다.")

        val user = getUser()
        AuthorizationUtils.validateUserIsManagerRole(user)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(user, noticeSession.notice.targetCourses.map { it.course.id }.toSet())

        val currentParticipantCount = noticeParticipantRepository.countParticipantBySessionId(sessionId)
        val participantCapacity =
            noticeSession.notice.participantCapacity ?: throw CustomBadRequestException("세션 참가 정원 데이터 에러")
        if (currentParticipantCount >= participantCapacity) {
            throw CustomBadRequestException("참가 정원이 다 찼습니다.")
        }

        val dtodata = NotificationDto(
            fromId = user.id,
            userId = participant.user.id,
            type = 6,
            url = "",
            content = noticeSession.notice.title,
            NotiType = 2,
            comment = "",
        )

        sseService.publish(dtodata)

        participant.status = ParticipantStatus.PARTICIPANT
        noticeParticipantRepository.save(participant)
    }

    /**
     * 세션 참가 거절
     * (WAIT -> REJECT or PARTICIPANT -> REJECT)
     *
     * @param sessionId 세션 ID
     * @param participantId 참가 신청 ID
     */
    @Transactional
    override fun rejectNoticeSessionApplication(sessionId: Long, participantId: Long) {
        val participant = noticeParticipantRepository.findByIdAndNoticeSessionId(participantId, sessionId)
            ?: throw CustomBadRequestException("참가요청이 존재하지 않습니다.")

        val noticeSession = noticeSessionRepository.findById(sessionId)
            .orElseThrow { CustomBadRequestException("해당 세션이 없습니다.") }

        val user = getUser()
        AuthorizationUtils.validateUserIsManagerRole(user)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(user, noticeSession.notice.targetCourses.map { it.course.id }.toSet())

        val dtodata = NotificationDto(
            fromId = user.id,
            userId = participant.user.id,
            type = 7,
            url = "",
            content = noticeSession.notice.title,
            NotiType = 2,
            comment = "",
        )

        sseService.publish(dtodata)

        participant.status = ParticipantStatus.REJECT
        noticeParticipantRepository.save(participant)
    }

    /**
     * 세션 참가 취소 (row 삭제)
     *
     * @param sessionId 세션 ID
     * @param participantId 참가 신청 ID
     */
    override fun cancelNoticeSessionParticipant(sessionId: Long, participantId: Long) {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")

        val participant = noticeParticipantRepository.findByIdAndNoticeSessionId(participantId, sessionId)
            ?: throw CustomBadRequestException("참가요청이 존재하지 않습니다.")

        noticeSessionRepository.findById(sessionId)
            .orElseThrow { CustomBadRequestException("해당 세션이 없습니다.") }

        if (participant.user.id != userId) {
            throw CustomBadRequestException("세션 참가 삭제에 대한 권한이 없습니다.")
        }

        val dtodata = NotificationDto(
            fromId = userId,
            userId = participant.noticeSession.notice.user.id,
            type = 5,
            url = "",
            content = participant.noticeSession.notice.title,
            NotiType = 2,
            comment = "",
        )

        sseService.publish(dtodata)

        noticeParticipantRepository.deleteById(participantId)
    }

    /**
     * 세션 참가자 조회
     * (searchParticipantStatus == null 이면 모든 상태 조회)
     *
     * @param sessionId 조회할 sessionId
     * @param pageable 세션 참가자 페이지네이션 요청 파라미터
     * @param searchParticipantStatus 세션 참가자 상태 검색 요청 파라미터
     */
    override fun getSessionParticipants(
        sessionId: Long,
        pageable: PageRequest,
        searchParticipantStatus: List<ParticipantStatus>
    ): Page<NoticeParticipantResponseDto> {
        val noticeSession = noticeSessionRepository.findById(sessionId)
            .orElseThrow { CustomBadRequestException("해당 세션이 존재하지 않습니다.") }

        val user = getUser()
        AuthorizationUtils.validateUserIsManagerRole(user)
        AuthorizationUtils.validateUserCourseContainAllTargetCourses(user, noticeSession.notice.targetCourses.map { it.course.id }.toSet())

        return noticeParticipantRepository.findBySessionIdAndStatusList(sessionId, searchParticipantStatus, pageable)
            .map { NoticeParticipantResponseDto(it) }
    }

    /**
     * 공지사항 좋아요 toggle
     *
     * @param noticeId 공지사항 ID
     */
    override fun toggleNoticeScrap(noticeId: Long): ToggleResponse {
        val userId = getUserId()

        val scrapedNotice = scrapedNoticeRepository.findByNoticeIdAndUserId(noticeId, userId)
        if (scrapedNotice == null) {
            val newScrapedNotice = ScrapedNoticeEntity(
                user = UserEntity(userId),
                notice = NoticeEntity(noticeId)
            )
            scrapedNoticeRepository.save(newScrapedNotice)
            return ToggleResponse(true)
        } else {
            scrapedNoticeRepository.deleteById(scrapedNotice.id)
            return ToggleResponse(false)
        }
    }

    /**
     * 현재 시간 이후로 마감일이 가장 가까운순으로 이벤트 세션 반환
     */
    override fun getApplicationCloseNotice(size: Long, days: Long): MutableList<NoticeCardDto>? {
        val user = getUser()

        return noticeRepository.getApplicationCloseNotice(user.id, size, days);
    }

    override fun getNoticeTitleById(linkedId: Long): String {
        val notice = noticeRepository.findById(linkedId)
            .orElseThrow { EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: $linkedId") }
        return notice.title ?: "No Title"
    }

    private fun getUserId(): Long {
        return securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")
    }

    private fun getUser(): UserEntity {
        val userId =  securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Not found user")
        return userRepository.findById(userId).orElseThrow { throw CustomBadRequestException("Not found user") }
    }

}