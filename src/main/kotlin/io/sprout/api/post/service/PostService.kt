package io.sprout.api.post.service

import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.notice.service.NoticeService
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.service.ProjectService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val projectService: ProjectService,
    private val noticeService: NoticeService
) {
    @Transactional
    fun createNoticePost(noticeRequestDto: NoticeRequestDto): Boolean {
        return try {
            val post = PostEntity(
                    postType = PostType.NOTICE
            )

            val projectId = noticeService.createNotice(noticeRequestDto)
            post.linkedId = projectId

            postRepository.save(post)

            true
        } catch (e: Exception) {
            false
        }
    }

    @Transactional
    fun createProjectPost(projectDto: ProjectRecruitmentRequestDto): Boolean {
        return try {
            val post = PostEntity(
                postType = PostType.PROJECT
            )

            val projectId = projectService.postProjectAndGetId(projectDto)
            post.linkedId = projectId

            postRepository.save(post)

            true
        } catch (e: Exception) {
            false
        }
    }

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
}