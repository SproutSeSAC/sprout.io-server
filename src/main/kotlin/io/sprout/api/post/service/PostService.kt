package io.sprout.api.post.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.dto.PostDto
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val projectService: ProjectService,
    private val noticeService: NoticeService
) {

    /**
     * 공지사항 추가
     * NoticeService의 createNotice를 호출합니다.
     */
    @Transactional
    fun createNoticePost(noticeRequestDto: NoticeRequestDto, clientId: Long): Boolean {
        return try {
            val post = PostEntity(
                clientId = clientId,
                postType = PostType.NOTICE
            )

            val projectId = noticeService.createNotice(noticeRequestDto)
            post.postType = PostType.NOTICE
            post.linkedId = projectId

            postRepository.save(post)

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 프로젝트 추가
     * projectService의 createProjectAndGetId를 호출합니다.
     * createProjectAndGetId는 이번에 새로 만든 녀석입니다. ID 매핑을 위해...
     * 원본 entity를 건드리지 않기 위해 일단은 이렇게 설정했습니다.
     */
    @Transactional
    fun createProjectPost(projectDto: ProjectRecruitmentRequestDto, clientId: Long): Boolean {
        return try {
            val post = PostEntity(
                clientId = clientId,
                postType = PostType.PROJECT
            )

            val projectId = projectService.postProjectAndGetId(projectDto)
            post.postType = PostType.PROJECT
            post.linkedId = projectId

            postRepository.save(post)

            true
        } catch (e: Exception) {
            false
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
                val noticeId = post.linkedId ?: throw IllegalArgumentException("테이블 매핑 에러")
                noticeService.getNoticeById(noticeId)
            }
            PostType.PROJECT -> {
                val projectId = post.linkedId ?: throw IllegalArgumentException("테이블 매핑 에러")
                projectService.findProjectDetailById(projectId)
                        ?: throw EntityNotFoundException("프로젝트를 찾을 수 없습니다. -> 공지는 원본 안 건드림")
            }
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
            }
        }
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
                }
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
    fun getNoticesByUserIdFromParticipant(userId: Long): List<PostEntity> {
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