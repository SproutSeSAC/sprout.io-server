package io.sprout.api.mypage.service

import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.entity.DummyPost
import io.sprout.api.mypage.entity.DummyPostComment
import io.sprout.api.mypage.entity.DummyPostParticipant
import io.sprout.api.mypage.entity.DummyPostScrap
import io.sprout.api.mypage.repository.*
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.service.PostService
import io.sprout.api.project.service.ProjectService
import io.sprout.api.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class MypageService(
        private val userRepository: UserRepository,
        private val userCampusRepository: UserCampusRepository,
        private val userCourseRepository: UserCourseRepository,
        private val dummyPostScrapRepository: DummyPostScrapRepository,
        private val dummyPostRepository: DummyPostRepository,
        private val dummyPostParticipantRepository: DummyPostParticipantRepository,
        private val dummyPostCommentRepository: DummyPostCommentRepository,
        private val postService: PostService,
        private val noticeService: NoticeService,
        private val projectService: ProjectService
) {

    // region [프로필 관련 API]

    fun getUserCard(userId: Long): CardDto.UserCard {
        // UserEntity 조회
        val user = userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("User not found") }

        // UserCampusEntity 조회
        val userCampus = userCampusRepository.findByUser_Id(userId)
                .orElseThrow { IllegalArgumentException("Campus not found") }

        // UserCourseEntity 조회
        val userCourse = userCourseRepository.findByUser_Id(userId)
                .orElseThrow { IllegalArgumentException("Course not found") }

        // ProfileCard 생성
        val profileCard = CardDto.ProfileCard(
                name = user.name,
                nickname = user.nickname,
                profileUrl = user.profileImageUrl
        )

        val CampusMini = CardDto.CampusMini(
                id = userCampus.campus.id,
                name = userCampus.campus.name
        )

        val CourseMini = CardDto.CourseMini(
                id = userCourse.course.id,
                name = userCourse.course.title
        )

        // StudyCard 생성
        val studyCard = CardDto.StudyCard(
                email = user.email,
                campus = CampusMini,
                course = CourseMini
        )

        // UserCard 생성
        return CardDto.UserCard(
                profile = profileCard,
                study = studyCard
        )
    }

    // 닉네임 변경
    fun updateNickname(userId: Long, request: UpdateNickNameDto) {
        val user = userRepository.findById(userId)
                .orElseThrow { EntityNotFoundException("유저 Id를 찾을 수 없음 : $userId") }

        user.nickname = request.nickname
        userRepository.save(user)
    }

    // 프사 변경
    fun updateProfileUrl(userId: Long, request: UpdateProfileUrlDto) {
        val user = userRepository.findById(userId)
                .orElseThrow { EntityNotFoundException("유저 Id를 찾을 수 없음 : $userId") }

        user.profileImageUrl = request.profileUrl
        userRepository.save(user)
    }

    // endregion

    // region [게시글 관련 API]

    // 작성 글 목록 조회
    fun getPostListByUserId(clientId: Long): List<PostDto> {
        val posts = postService.getPostsByClientId(clientId)

        // DTO 변환
        return posts.map { post ->
            val title = when (post.postType) {
                PostType.NOTICE -> {
                    val linkedId = post.linkedId
                            ?: throw IllegalArgumentException("테이블 매핑 오류")
                    noticeService.getNoticeTitleById(linkedId)
                }
                PostType.PROJECT -> {
                    val linkedId = post.linkedId
                            ?: throw IllegalArgumentException("테이블 매핑 오류")
                    projectService.getProjectTitleById(linkedId)
                }
            }

            PostDto(
                    postId = post.id,
                    clientId = post.clientId,
                    postType = post.postType.name,
                    title = title,
                    createdAt = post.createdAt,
                    updatedAt = post.updatedAt
            )
        }
    }

    // 댓글 조회
    fun getPostCommentListByUserId(userId: Int): List<PostCommentDto> {
        val comments: List<DummyPostComment> = dummyPostCommentRepository.findAllByUserId(userId)

        // DTO 변환
        return comments.map {
            PostCommentDto(
                    commentId = it.commentId,
                    userId = it.userId,
                    postId = it.postId
            )
        }
    }

    // 찜한 글 목록 조회
    fun getPostScrapListByUserId(userId: Int): List<PostScrapDto> {
        val scraps: List<DummyPostScrap> = dummyPostScrapRepository.findAllByUserId(userId)

        // DTO 변환
        return scraps.map {
            PostScrapDto(
                    postScrapId = it.postscrapid,
                    userId = it.userId
            )
        }
    }

    // 찜한글 삭제
    fun deletePostScrap(scrapid: Int, userId: Int) {
        dummyPostScrapRepository.deleteBypostscrapidAndUserId(scrapid, userId)
    }

    // 참여 글 목록 조회
    fun getPostParticipantListByUserId(userId: Int): List<PostParticipantDto> {
        val particis: List<DummyPostParticipant> = dummyPostParticipantRepository.findAllByUserId(userId)

        // DTO 변환
        return particis.map {
            PostParticipantDto(
                    postParticipantid = it.postparticipantid,
                    userId = it.userId
            )
        }
    }

    // 참여글 삭제
    fun deletePostParticipant(postparticipantId: Int, userId: Int) {
        dummyPostParticipantRepository.deleteByPostparticipantidAndUserId(postparticipantId, userId)
    }
    // endregion
}