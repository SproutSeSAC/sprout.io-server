package io.sprout.api.mypage.service

import io.sprout.api.comment.service.CommentService
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.entity.DummyPostParticipant
import io.sprout.api.mypage.repository.*
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.service.PostService
import io.sprout.api.project.service.ProjectService
import io.sprout.api.scrap.service.ScrapService
import io.sprout.api.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class MypageService(
        private val userRepository: UserRepository,
        private val userCourseRepository: UserCourseRepository,
        private val courseRepository: CourseRepository,
        private val dummyPostParticipantRepository: DummyPostParticipantRepository,
        private val postService: PostService,
        private val noticeService: NoticeService,
        private val projectService: ProjectService,
        private val commentService: CommentService,
        private val scrapService: ScrapService
) {

    // region [프로필 관련 API]

    fun getUserCard(userId: Long): CardDto.UserCard {
        // UserEntity 조회
        val user = userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("User not found") }

        // ProfileCard 생성
        val profileCard = CardDto.ProfileCard(
                name = user.name,
                nickname = user.nickname,
                profileUrl = user.profileImageUrl
        )

        val CampusCard = courseRepository.findUserCampusByUserId(userId).map {
            CardDto.CampusInfo(
                id = it.id,
                campusName = it.name
            )
        }

        val CourseCard = userCourseRepository.findByUser_Id(userId).map {
            CardDto.CourseInfo(
                id = it.course.id,
                courseName = it.course.title
            )
        }

        // StudyCard 생성
        val studyCard = CardDto.StudyCard(
                email = user.email,
                campus = CampusCard,
                course = CourseCard
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
    fun getPostCommentListByUserId(clientId: Long): List<PostCommentDto> {
        val comments = commentService.getCommentsByClientId(clientId)

        return comments.map {
            PostCommentDto(
                    commentId = it.id,
                    userId = it.userId,
                    postId = it.postId,
                    content = it.content
            )
        }
    }

    // 찜한 글 목록 조회
    fun getPostScrapListByUserId(clientId: Long): List<PostScrapDto> {
        val scraps = scrapService.getScrapsByUserId(clientId)

        return scraps.map {
            PostScrapDto(
                    postScrapId = it.id,
                    userId = it.userId,
                    postId = it.postId,
                    createdAt = it.createdAt
            )
        }
    }

    // 참여 글 목록 조회
    fun getPostParticipantListByUserId(userId: Long): List<PostParticipantDto> {
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
    fun deletePostParticipant(postparticipantId: Int, userId: Long) {
        dummyPostParticipantRepository.deleteByPostparticipantidAndUserId(postparticipantId, userId)
    }
    // endregion
}