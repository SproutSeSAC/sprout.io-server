package io.sprout.api.post.service

import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.service.MealPostService
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.dto.PostDto
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import io.sprout.api.scrap.service.ScrapService
import io.sprout.api.store.service.StoreService
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val projectService: ProjectService,
    private val noticeService: NoticeService,
    private val mealPostService: MealPostService,
    private val scrapService: ScrapService,
    private val storeService: StoreService
) {

    /**
     * 공지사항 추가
     * NoticeService의 createNotice를 호출합니다.
     * @return Pair<생성 된 개체 ID, 생성 된 부모 게시글 ID>
     */
    @Transactional
    fun createNoticePost(noticeRequestDto: NoticeRequestDto, clientId: Long): Pair<Long, Long> {
        return try {
            val noticeId = noticeService.createNotice(noticeRequestDto)
            val post = PostEntity(
                clientId = clientId,
                postType = PostType.NOTICE,
                linkedId = noticeId
            )

            postRepository.save(post)

            Pair(noticeId, post.id)
        } catch (e: Exception) {
            throw RuntimeException("공지사항 생성 중 오류 발생", e)
        }
    }

    /**
     * 프로젝트 추가
     * projectService의 createProjectAndGetId를 호출합니다.
     * createProjectAndGetId는 이번에 새로 만든 녀석입니다. ID 매핑을 위해...
     * 원본 entity를 건드리지 않기 위해 일단은 이렇게 설정했습니다.
     * @return Pair<생성 된 개체 ID, 생성 된 부모 게시글 ID>
     */
    @Transactional
    fun createProjectPost(projectDto: ProjectRecruitmentRequestDto, clientId: Long): Pair<Long, Long> {
        return try {
            val projectId = projectService.postProjectAndGetId(projectDto)
            val post = PostEntity(
                clientId = clientId,
                postType = PostType.PROJECT,
                linkedId = projectId
            )

            postRepository.save(post)

            Pair(projectId, post.id)
        } catch (e: Exception) {
            throw RuntimeException("프로젝트 생성 중 오류 발생", e)
        }
    }

    /**
     * 한끼팟 추가
     * meealService의 createNotice를 호출합니다.
     * @return Pair<생성 된 개체 ID, 생성 된 부모 게시글 ID>
     */
    @Transactional
    fun createMealPost(dto: MealPostDto.MealPostCreateRequest, clientId: Long): Pair<Long, Long> {
        return try {
            val mealId = mealPostService.createMealPostReturnId(dto)
            val post = PostEntity(
                clientId = clientId,
                postType = PostType.MEAL,
                linkedId = mealId
            )

            postRepository.save(post)

            Pair(mealId, post.id)
        } catch (e: Exception) {
            throw RuntimeException("MealPost 생성 중 오류 발생", e)
        }
    }


    /**
     * 게시글 읽기
     * 입력 ID에 맞는 dto값을 반환합니다.
     */
    @Transactional
    fun getPostById(postId: Long): Any {
        val post = postRepository.findById(postId)
                .orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        return when (post.postType) {
            PostType.NOTICE -> {
                val noticeId = post.linkedId
                noticeService.getNoticeById(noticeId)
            }
            PostType.PROJECT -> {
                val projectId = post.linkedId
                projectService.findProjectDetailById(projectId)
                        ?: throw EntityNotFoundException("프로젝트를 찾을 수 없습니다. -> 공지는 원본 안 건드림")
            }
            PostType.MEAL -> {
                val mealId = post.linkedId
                mealPostService.getMealPostDetail(mealId)
            }
            PostType.STORE -> {
                val storeId = post.linkedId
                storeService.getStoreDetail(storeId)
            }
            else -> throw EntityNotFoundException("존재하지 않는 형식입니다.")
        }
    }

    /**
     * 게시글 작성자 ID 반환
     * NOTICE, PROJECT만 가능
     */
    @Transactional
    fun getPostWriterById(postId: Long): Long? {
        val post = postRepository.findById(postId)
            .orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        return when (post.postType) {
            PostType.NOTICE -> {
                val noticeId = post.linkedId
                noticeService.getNoticeById(noticeId).writer.userId
            }
            PostType.PROJECT -> {
                val projectId = post.linkedId
                projectService.findProjectDetailById(projectId)?.writerId
            }
            else -> throw EntityNotFoundException("존재하지 않는 형식입니다.")
        }
    }

    /**
     * 타입별 게시글 읽기
     * 입력 TYPE에 맞는 값을 반환합니다.
     */
    @Transactional
    fun getPostsByPostType(type: PostType, compact: Boolean): List<Any> {
        val posts = postRepository.findByPostType(type)

        return posts.map { post ->
            when (post.postType) {
                PostType.NOTICE -> {
                    val noticeId = post.linkedId ?: throw IllegalArgumentException("테이블 매핑 에러")
                    val notice = noticeService.getNoticeById(noticeId)
                    if (compact) {
                        mapOf(
                                "id" to post.id,
                                "noticeid" to post.linkedId,
                                "title" to (notice.title ?: "No title"),
                                "content" to (notice.content ?: "No content")
                        )
                    } else {
                        notice
                    }
                }
                PostType.PROJECT -> {
                    val projectId = post.linkedId ?: throw IllegalArgumentException("테이블 매핑 에러")
                    val project = projectService.findProjectDetailById(projectId)
                            ?: throw EntityNotFoundException("프로젝트를 찾을 수 없습니다.")
                    if (compact) {
                        mapOf(
                                "id" to post.id,
                                "projectid" to post.linkedId,
                                "title" to (project.title ?: "No title"),
                                "content" to (project.writerNickName ?: "No NickName")
                        )
                    } else {
                        project
                    }
                }
                PostType.MEAL -> {
                    val mealId = post.linkedId
                    val meal = mealPostService.getMealPostDetail(mealId)

                    meal
                }
                else -> null
            }
        }.filterNotNull()
    }

    /**
     * 게시글 수정
     * 입력 id에 맞게 dto를 완전히 덮어씌웁니다.
     */
    @Transactional
    fun updatePost(postId: Long, dto: Any): Boolean {
        val post = postRepository.findById(postId)
                .orElseThrow { EntityNotFoundException("게시글을 찾을 수 없습니다.") }

        val linkedId = post.linkedId ?: throw IllegalArgumentException("테이블 매핑 에러")

        return try {
            when (post.postType) {
                PostType.NOTICE -> {
                    if (dto !is NoticeRequestDto) {
                        throw IllegalArgumentException("Notice DTO를 확인 해 주세요.")
                    }
                    noticeService.updateNotice(linkedId, dto)
                    true
                }

                PostType.PROJECT -> {
                    if (dto !is ProjectRecruitmentRequestDto) {
                        throw IllegalArgumentException("Project DTO를 확인 해 주세요.")
                    }
                    projectService.updateProject(linkedId, dto)
                    true
                }

                PostType.MEAL -> {
                    if (dto !is MealPostDto.MealPostCreateRequest) {
                        throw IllegalArgumentException("Meal DTO를 확인 해 주세요.")
                    }

                    // 내용 할지 말지 차후 이야기..

                    true
                }

                else -> false
            }
        } catch (e: Exception) {
            println("업데이트 실패 : ${e.message}")
            false
        }
    }

    /**
     * 게시글 삭제
     * 삭제합니다. notice와 project는 jpa 의존으로 묶어놨습니다.
     */
    @Transactional
    fun deletePost(postId: Long): Boolean {
        return try {
            val post = postRepository.findById(postId).orElseThrow {
                IllegalArgumentException("$postId 없음")
            }

            when (post.postType) {
                PostType.PROJECT -> projectService.deleteProject(post.linkedId)
                PostType.NOTICE -> noticeService.deleteNotice(post.linkedId)
                PostType.MEAL -> mealPostService.deleteMealPost(post.linkedId)
                else -> null
            }

            scrapService.deleteAllScrapsWithPostId(postId)
            postRepository.delete(post)
            true
        } catch (e: Exception) {
            println("삭제 실패 : ${e.message}")
            false
        }
    }

    /**
     * Linked ID 읽기
     * 해당 POST ID에 맞는 LINK ID를 가져오는 메서드입니다.
     */
    @Transactional
    fun getLinkedIdByPostId(postId: Long): Long {
        val post = postRepository.findById(postId)
                .orElseThrow { EntityNotFoundException("존재하지 않는 게시글 ID: $postId") }

        return post.linkedId ?: throw IllegalArgumentException("테이블 매핑 오류")
    }

    @Transactional
    fun getPostByLinkedIdAndPostType(linkedId: Long, postType: PostType): PostEntity {
        return postRepository.findByLinkedIdAndPostType(linkedId, postType) ?: throw IllegalArgumentException("읽을 수 없음");
    }

    @Transactional
    fun getPostTitle(postId: Long): String {
        val post = postRepository.findById(postId)
            .orElseThrow { EntityNotFoundException("존재하지 않는 게시글 ID: $postId") }

        when (post.postType) {
            PostType.NOTICE -> {
                return noticeService.getNoticeById(post.linkedId).title
            }
            PostType.PROJECT -> {
                return projectService.findProjectDetailById(post.linkedId)?.title ?: ""
            }
            PostType.MEAL -> {
                return mealPostService.getMealPostDetail(post.linkedId).title
            }
            else -> throw EntityNotFoundException("프로젝트를 찾을 수 없습니다. -> 공지는 원본 안 건드림")
        }
    }

    /**
     * 내가 작성한 글 목록 읽기
     */
    @Transactional()
    fun getPostsByClientId(clientId: Long): List<PostEntity> {
        return postRepository.findAllByClientId(clientId)
    }

    /**
     * 참여 글 목록 읽기 (ID만)
     */
    fun getNoticeIdsByUserIdFromParticipant(userId: Long): List<Long> {
        return postRepository.findNoticeIdsByUserIdFromParticipant(userId)
    }

    /**
     * 참여 글 목록 읽기 (전체)
     */
    fun getNoticesByUserIdFromParticipant(userId: Long): List<NoticeParticipantEntity> {
        return postRepository.findNoticesByUserIdFromParticipant(userId)
    }


    /**
     * 찜한 글 가져오기
     */
//    @Transactional()
//    fun getscraplist(clientId: Long): PostDto.ScrapList {
//
//    }
}