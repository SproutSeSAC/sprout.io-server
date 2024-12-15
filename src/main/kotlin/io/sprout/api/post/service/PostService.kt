package io.sprout.api.post.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.entity.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool

@Service
class PostService(
    private val postRepository: PostRepository,
    private val noticeService: NoticeService,
    private val projectService: ProjectService
) {
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
}