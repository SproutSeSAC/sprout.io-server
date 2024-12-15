package io.sprout.api.post.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.dto.PostResponseDto
import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.entity.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val noticeService: NoticeService,
    private val projectService: ProjectService
) {
    fun getOrigin(Post_id: Long): Long {
        val post = postRepository.findById(Post_id)
        if (post.isPresent) {
            return post.get().referenceId
        }

        throw IllegalArgumentException("찾을 수 없는 게시글")
    }

    fun createNotice(noticeRequestDto: NoticeRequestDto): PostEntity {
        val ref_id = noticeService.createNotice(noticeRequestDto)
        val post = PostEntity(
            postType = PostType.NOTICE,
            referenceId = ref_id
        )
        return postRepository.save(post)
    }

    fun createProject(projectRecruitmentRequestDto: ProjectRecruitmentRequestDto): PostEntity {
        val ref_id = projectService.createProject(projectRecruitmentRequestDto)
        val post = PostEntity(
            postType = PostType.PROJECT,
            referenceId = ref_id
        )
        return postRepository.save(post)
    }

    fun deletePost(post_id: Long): Boolean {
        val post = postRepository.findById(post_id)

        if (post.isPresent) {
            when (post.get().postType) {
                PostType.NOTICE -> noticeService.deleteNotice(post.get().referenceId)
                PostType.PROJECT -> projectService.deleteProject(post.get().referenceId)
            }
            postRepository.deleteById(post_id)
            return true
        }
        return false
    }

    fun getPost(post_id: Long): Any? {
        val post = postRepository.findById(post_id)
        if (post.isPresent) {
            return when (post.get().postType) {
                PostType.NOTICE -> noticeService.getNoticeById(post.get().referenceId)
                PostType.PROJECT -> projectService.findProjectDetailById(post.get().referenceId)
            }
        }
        return false
    }

    fun getTitlesByPostType(postType: String, pageable: Pageable): Page<PostResponseDto> {
        return when (postType.uppercase()) {
            "NOTICE" -> {
                val notices = noticeService.getNoticeDataWithPagination(pageable)
                notices.map { (refId, title) ->
                    val post = postRepository.findByPostTypeAndReferenceId(PostType.NOTICE, refId)
                        ?: throw IllegalArgumentException("찾을 수 없는 게시글 : $refId")
                    PostResponseDto(
                        id = post.id,
                        title = title,
                        refId = refId
                    )
                }
            }
            "PROJECT" -> {
                val projects = projectService.getProjectDataWithPagination(pageable)
                projects.map { (refId, title) ->
                    val post = postRepository.findByPostTypeAndReferenceId(PostType.PROJECT, refId)
                        ?: throw IllegalArgumentException("찾을 수 없는 게시글 : $refId")
                    PostResponseDto(
                        id = post.id,
                        title = title,
                        refId = refId
                    )
                }
            }
            else -> throw IllegalArgumentException("잘못된 타입 : $postType")
        }
    }
}