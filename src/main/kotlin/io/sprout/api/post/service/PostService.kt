package io.sprout.api.post.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.entity.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import org.springframework.stereotype.Service

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
}