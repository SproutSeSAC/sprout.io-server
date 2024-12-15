package io.sprout.api.comment.service

import io.sprout.api.comment.dto.CommentRequestDto
import io.sprout.api.comment.entity.CommentEntity
import io.sprout.api.comment.repository.CommentRepository
import io.sprout.api.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository
) {

    @Transactional
    fun addCommentToPost(dto: CommentRequestDto): CommentEntity {
        val post = postRepository.findById(dto.postId).orElseThrow { IllegalArgumentException("찾을 수 없는 게시글") }
        val comment = CommentEntity(content = dto.content, post = post)
        commentRepository.save(comment)
        return comment
    }

    @Transactional
    fun deleteComment(commentId: Long) {
        val comment = commentRepository.findById(commentId).orElseThrow { IllegalArgumentException("댓글이 존재하지 않음") }
        commentRepository.delete(comment)
    }
}
