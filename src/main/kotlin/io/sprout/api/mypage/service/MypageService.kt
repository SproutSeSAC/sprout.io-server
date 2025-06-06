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
import io.sprout.api.project.model.dto.ProjectDetailResponseDto
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.entities.PType
import io.sprout.api.project.service.ProjectService
import io.sprout.api.scrap.service.ScrapService
import io.sprout.api.store.repository.StoreReviewRepository
import io.sprout.api.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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
    private val scrapService: ScrapService,
    private val storeReviewRepository: StoreReviewRepository,
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
    fun getPostListByUserId(clientId: Long, pageable: Pageable, postTypes: List<String>?): Page<PostAndNickNameDto> {
        val posts: Page<PostEntity>

        if (postTypes.isNullOrEmpty()) {
            posts = postService.getPostsByClientIdAndPage(clientId, pageable)
        } else {
            val filteredType: List<PostType> = postTypes.mapNotNull { item ->
                when (item.uppercase()) {
                    "NOTICE" -> PostType.NOTICE
                    "PROJECT" -> PostType.PROJECT
                    "STUDY" -> PostType.PROJECT
                    "MEAL" -> PostType.MEAL
                    else -> null
                }
            }

            val projectType: List<PType> = postTypes.mapNotNull { item ->
                when (item.uppercase()) {
                    "PROJECT" -> PType.PROJECT
                    "STUDY" -> PType.STUDY
                    else -> null
                }
            }

            posts = postService.getPostsByClientIdAndPageAndPTypeAndPostTypeIn(clientId, filteredType, projectType, pageable)
        }

        return posts.map { post ->
            var projectType = ""
            val title = when (post.postType) {
                PostType.NOTICE -> {
                    try {
                        noticeService.getNoticeTitleById(post.linkedId)
                    } catch (e: Exception) {
                        println("NOTICE ERROR: id=${post.linkedId}, message=${e.message}")
                        "공지사항을 찾을 수 없음."
                    }
                }
                PostType.PROJECT -> {
                    val linkedId = post.linkedId
                    try {
                        val project = projectService.getProjectById(linkedId)
                        if (project !== null) {
                            projectType = project.pType.toString()
                            project.title
                        } else null
                    } catch (e: Exception) {
                        println("PROJECT ERROR: linkedId=$linkedId, message=${e.message}")
                        "프로젝트(스터디)를 찾을 수 없음."
                    }
                }
                PostType.MEAL -> {
                    try {
                        mealPostService.getMealPostDetail(post.linkedId).title
                    } catch (e: Exception) {
                        println("MEAL ERROR: id=${post.linkedId}, message=${e.message}")
                        "한끼팟 게시글을 찾을 수 없음."
                    }
                }
                else -> null
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
                createdNickName = user.nickname,
                pType = projectType
            )
        }.filterNotNull()
            .let { content ->
                PageImpl(content, pageable, posts.totalElements)
            }
    }

    // 댓글 조회
    fun getPostCommentListByUserId(
        clientId: Long,
        pageable: Pageable,
        postTypes: List<PostType>?
    ): Page<PostCommentDto> {
        val storeReviews = storeReviewRepository.findByUserId(clientId)
        val comments = commentService.getCommentsByClientId(clientId)

        val storeReviewComments = storeReviews.map {
            PostCommentDto(
                commentId = it.id,
                userNickname = it.user.nickname,
                postId = postService.getPostByLinkedIdAndPostType(it.store.id, PostType.STORE).id,
                content = it.review ?: "",
                createdAt = it.createdAt,
                postType = PostType.STORE.toString(),
                pType = ""
            )
        }

        val postComments = comments.map {
            var projectType = ""
            val post = postService.getPostById(it.postId)
            val mPostType = when (post) {
                is NoticeDetailResponseDto -> PostType.NOTICE
                is ProjectDetailResponseDto -> {
                    projectType = post.pType.toString()
                    PostType.PROJECT
                }
                is MealPostDto.MealPostDetailResponse -> PostType.MEAL
                else -> PostType.NOTICE
            }

            PostCommentDto(
                commentId = it.id,
                userNickname = it.userInfo.nickname,
                postId = it.postId,
                content = it.content,
                createdAt = it.createAt,
                postType = mPostType.toString(),
                pType = projectType
            )
        }

        val combined = (storeReviewComments + postComments)

        val filtered = if (postTypes.isNullOrEmpty()) {
            combined
        } else {
            val stringPostTypes = postTypes.map { it.toString() }
            combined.filter { stringPostTypes.contains(it.postType) }
        }

        val sorted = filtered.sortedByDescending { it.createdAt }

        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(sorted.size)
        val pageContent = if (start >= end) emptyList() else sorted.subList(start, end)

        return PageImpl(pageContent, pageable, sorted.size.toLong())
    }


    // 찜한 글 목록 조회
    fun getPostScrapListByUserId(clientId: Long, pageable: Pageable?): Page<GetPostResponseDto> {
        val scraps = scrapService.getScrapsByUserId(clientId)

        val scrapPosts = scraps.mapNotNull {
            val post = postService.getPostById(it.postId)
            val userId = postService.getPostWriterById(it.postId)

            if (userId == null) {
                null
            }
            else {
                val user = userRepository.findUserById(userId);

                if (user != null) {
                    val writer = writerDto(
                        name = user.name ?: "",
                        nickname = user.nickname,
                        profileImg = user.profileImageUrl ?: ""
                    )

                    var projectType = ""

                    val postData = when (post) {
                        is NoticeDetailResponseDto -> PostInfoDto(post.title, post.content, PostType.NOTICE)
                        is ProjectDetailResponseDto -> {
                            projectType = post.pType.toString()
                            PostInfoDto(post.title, post.description, PostType.PROJECT)
                        }
                        is MealPostDto.MealPostDetailResponse -> {
                            PostInfoDto(post.title, "", PostType.MEAL)
                        }
                        else -> return@mapNotNull null
                    }

                    GetPostResponseDto(
                        id = it.id,
                        writer = writer,
                        postId = it.postId,
                        title = postData.title,
                        postType = postData.postType,
                        content = postData.content,
                        pType = projectType
                    )
                }
                else {
                    null
                }
            }
        }

        if (pageable == null) {
            return PageImpl(scrapPosts, Pageable.unpaged(), scrapPosts.size.toLong())
        }

        val start = pageable.offset.toInt().coerceIn(0, scrapPosts.size)
        val end = (start + pageable.pageSize).coerceAtMost(scrapPosts.size)
        val pagedList = if (start <= end) scrapPosts.subList(start, end) else emptyList<GetPostResponseDto>()

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
    fun getPostParticipantListByUserId(userId: Long): ParticipantListResponseDto {
        val detailPosts: List<NoticeParticipantEntity> = postService.getNoticesByUserIdFromParticipant(userId)

        val now = LocalDateTime.now()

        val nearList = detailPosts
            .filter { it.noticeSession.eventStartDateTime.isAfter(now) }
            .sortedBy { it.noticeSession.eventStartDateTime }
            .take(4)
            .map { data ->
                ParticipantDto(
                    postId = postService
                        .getPostByLinkedIdAndPostType(data.noticeSession.notice.id, PostType.NOTICE)
                        .id,
                    sessionId = data.noticeSession.id,
                    participantId = data.id,
                    participantStatus = data.status,
                    title = data.noticeSession.notice.title,
                    role = data.noticeSession.notice.user.role,
                    ordinal = data.noticeSession.ordinal,
                    startDateTime = data.noticeSession.eventStartDateTime,
                    endDateTime = data.noticeSession.eventEndDateTime,
                    satisfactionSurvey = data.noticeSession.notice.satisfactionSurvey,
                    meetingPlace = data.noticeSession.notice.meetingPlace,
                    meetingType = data.noticeSession.notice.meetingType,
                )
            }

        val participantDtoList = detailPosts
            .sortedByDescending { it.noticeSession.id }
            .map { data ->
                ParticipantDto(
                    postId = postService
                        .getPostByLinkedIdAndPostType(data.noticeSession.notice.id, PostType.NOTICE)
                        .id,
                    sessionId = data.noticeSession.id,
                    participantId = data.id,
                    participantStatus = data.status,
                    title = data.noticeSession.notice.title,
                    role = data.noticeSession.notice.user.role,
                    ordinal = data.noticeSession.ordinal,
                    startDateTime = data.noticeSession.eventStartDateTime,
                    endDateTime = data.noticeSession.eventEndDateTime,
                    satisfactionSurvey = data.noticeSession.notice.satisfactionSurvey,
                    meetingPlace = data.noticeSession.notice.meetingPlace,
                    meetingType = data.noticeSession.notice.meetingType,
                )
            }

        return ParticipantListResponseDto(nearList, participantDtoList)
    }

    // endregion
}