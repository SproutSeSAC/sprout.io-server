package io.sprout.api.mypage.service

import io.sprout.api.comment.service.CommentService
import io.sprout.api.course.infra.CourseRepository
import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.service.MealPostService
import io.sprout.api.mypage.dto.*
import io.sprout.api.mypage.repository.*
import io.sprout.api.notice.model.dto.NoticeDetailResponseDto
import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.service.PostService
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.service.ProjectService
import io.sprout.api.scrap.service.ScrapService
import io.sprout.api.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.awt.dnd.DropTarget

@Service
class MypageService(
        private val userRepository: UserRepository,
        private val userCourseRepository: UserCourseRepository,
        private val courseRepository: CourseRepository,
        private val postService: PostService,
        private val noticeService: NoticeService,
        private val projectService: ProjectService,
        private val mealPostService: MealPostService,
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
                phoneNumber = user.phoneNumber,
                profileUrl = user.profileImageUrl
        )

        val CampusCard = courseRepository.findUserCampusByUserId(userId).map {
            CardDto.CampusInfo(
                id = it.id,
                campusName = it.name
            )
        }

        val CourseCard = userCourseRepository.findByUserId(userId).map {
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
    fun getPostListByUserId(clientId: Long): List<PostAndNickNameDto> {
        val posts = postService.getPostsByClientId(clientId)

        // DTO 변환
        return posts.map { post ->
            val title = when (post.postType) {
                PostType.NOTICE -> {
                    val linkedId = post.linkedId
                    noticeService.getNoticeTitleById(linkedId)
                }
                PostType.PROJECT -> {
                    val linkedId = post.linkedId
                    projectService.getProjectTitleById(linkedId)
                }
                PostType.MEAL -> {
                    val linkedId = post.linkedId
                    mealPostService.getMealPostDetail(linkedId)
                }
            }

            val user = userRepository.findById(post.clientId)
                .orElseThrow { EntityNotFoundException("유저 Id를 찾을 수 없음") }

            PostAndNickNameDto(
                postId = post.id,
                linkedId = post.linkedId,
                clientId = post.clientId,
                postType = post.postType.name,
                title = title.toString(),
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                createdNickName = user.nickname
            )
        }
    }

    // 댓글 조회
    fun getPostCommentListByUserId(clientId: Long): List<PostCommentDto> {
        val comments = commentService.getCommentsByClientId(clientId)

        return comments.map {
            PostCommentDto(
                commentId = it.id,
                userNickname = it.userInfo.nickname,
                postId = it.postId,
                content = it.content,
            )
        }
    }

    // 찜한 글 목록 조회
    fun getPostScrapListByUserId(clientId: Long, pageable: Pageable?): Page<GetPostResponseDto> {
        val scraps = scrapService.getScrapsByUserId(clientId)

        val scrapPosts = scraps.mapNotNull {
            val user = userRepository.findUserById(it.userId)
            val post = postService.getPostById(it.postId)

            if (user != null && post != null) {
                val writer = writerDto(
                    name = user.name ?: "",
                    nickname = user.nickname,
                    profileImg = user.profileImageUrl ?: ""
                )

                val postData = when (post) {
                    is NoticeDetailResponseDto -> PostInfoDto(post.title, post.content, PostType.NOTICE)
                    is ProjectResponseDto -> PostInfoDto(post.title, post.description, PostType.PROJECT)
                    is MealPostDto.MealPostDetailResponse -> PostInfoDto(post.title, "", PostType.MEAL)
                    else -> return@mapNotNull null
                }

                GetPostResponseDto(
                    id = it.id,
                    writer = writer,
                    postId = it.postId,
                    title = postData.title,
                    postType = postData.postType,
                    content = postData.content
                )
            } else {
                null
            }
        }

        if (pageable == null) {
            return PageImpl(scrapPosts, Pageable.unpaged(), scrapPosts.size.toLong())
        }

        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, scrapPosts.size)
        val pagedList = scrapPosts.subList(start, end)

        return PageImpl(pagedList, pageable, scrapPosts.size.toLong())
    }

    // 참여 글 목록 조회 (제목만)
    fun getPostParticipantListTitleByUserId(userId: Long): List<String> {
        val DetailPost: List<NoticeParticipantEntity> = postService.getNoticesByUserIdFromParticipant(userId)

        val participantsNames: List<String> = DetailPost.map { data ->
            data.noticeSession.notice.title
        }

        return participantsNames
    }

    // 참여 글 데이터 전체 조회 (전체)
    fun getPostParticipantListByUserId(userId: Long): List<ParticipantDto> {
        val DetailPost: List<NoticeParticipantEntity> = postService.getNoticesByUserIdFromParticipant(userId)
        val participant: List<ParticipantDto> = DetailPost.map { data ->
            ParticipantDto(
                title = data.noticeSession.notice.title,
                id = data.noticeSession.notice.id,
                participantId = data.id
            )
        }

        return participant
    }
    // endregion
}