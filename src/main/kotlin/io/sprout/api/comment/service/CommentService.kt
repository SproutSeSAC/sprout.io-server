package io.sprout.api.comment.service

import io.sprout.api.comment.entity.CommentEntity
import io.sprout.api.comment.dto.CommentRequestDto
import io.sprout.api.comment.dto.CommentResponseDto
import io.sprout.api.comment.dto.commentUserDto
import io.sprout.api.comment.repository.CommentRepository
import io.sprout.api.notification.entity.NotificationDto
import io.sprout.api.post.entities.PostType
import io.sprout.api.post.repository.PostRepository
import io.sprout.api.post.service.PostService
import io.sprout.api.sse.service.SseService
import io.sprout.api.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentService(
        private val commentRepository: CommentRepository,
        private val userRepository: UserRepository,
        private val postRepository: PostRepository,
        private val sseService: SseService,
        private val postService: PostService
) {
    @Transactional
    fun createComment(clientID: Long, dto: CommentRequestDto): CommentResponseDto {
        val user = userRepository.findUserById(clientID)
                ?: throw EntityNotFoundException("유저를 찾을 수 없음")

        val post = postRepository.findById(dto.postId)
                .orElseThrow { EntityNotFoundException("게시글을 찾을 수 없음") }

        val isNotice = if (post.postType == PostType.NOTICE) 3 else 2

        val comment = CommentEntity(
                content = dto.content,
                user = user,
                post = post,
                imgurl = dto.imgUrl,
                createdAt = LocalDateTime.now()
        )
        val savedComment = commentRepository.save(comment)

        val dtodata = NotificationDto(
            fromId = clientID,
            userId = post.clientId,
            type = isNotice.toLong(),
            url = "${post.id}",
            content = postService.getPostTitle(post.id),
            NotiType = 5,
            comment = dto.content,
        )

        sseService.publish(dtodata)
        return convertToResponseDto(savedComment)
    }

    @Transactional(readOnly = true)
    fun getCommentById(commentId: Long): CommentResponseDto {
        val comment = commentRepository.findById(commentId)
                .orElseThrow { EntityNotFoundException("코멘트가 없음 (조회실패)") }
        return convertToResponseDto(comment)
    }

    @Transactional(readOnly = true)
    fun getAllComments(): List<CommentResponseDto> {
        return commentRepository.findAll().map { convertToResponseDto(it) }
    }

    @Transactional
    fun updateComment(clientID: Long, commentId: Long, dto: CommentRequestDto): CommentResponseDto {
        val user = userRepository.findUserById(clientID)
                ?: throw EntityNotFoundException("유저를 찾을 수 없음")

        val comment = commentRepository.findById(commentId)
                .orElseThrow { EntityNotFoundException("코멘트가 없음 (조회실패)") }

        if (comment.user.id != user.id) {
            throw IllegalAccessException("본인 댓글로 인증되지 않음")
        }

        val post = postRepository.findById(dto.postId)
                .orElseThrow { EntityNotFoundException("게시글을 찾을 수 없음") }

        comment.content = dto.content
        comment.post = post

        val savedComment = commentRepository.save(comment)
        return convertToResponseDto(savedComment)
    }

    @Transactional
    fun deleteComment(clientID: Long, commentId: Long): Boolean {
        val user = userRepository.findUserById(clientID)
                ?: return false

        val comment = commentRepository.findById(commentId)
                .orElseThrow { EntityNotFoundException("코멘트가 없음 (조회실패)") }

        if (comment.user.id != user.id) {
            return false
        }

        commentRepository.delete(comment)
        return true
    }

    @Transactional(readOnly = true)
    fun getCommentsByPostId(postId: Long): List<CommentResponseDto> {
        val comments = commentRepository.findByPostId(postId)
        return comments.map { comment ->


            CommentResponseDto(
                id = comment.id,
                content = comment.content,
                userInfo = commentUserDto(
                    nickname = comment.user.nickname,
                    profileImg = comment.user.profileImageUrl ?: ""
                ),
                postId = comment.post.id,
                imgUrl = comment.imgurl,
                createAt = comment.createdAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getCommentsByClientId(clientID: Long): List<CommentResponseDto> {
        val user = userRepository.findUserById(clientID)
                ?: throw EntityNotFoundException("유저를 찾을 수 없음")

        val comments = commentRepository.findAllByUser_Id(clientID)

        return comments.map { convertToResponseDto(it) }
    }

    private fun convertToResponseDto(comment: CommentEntity): CommentResponseDto {
        return CommentResponseDto(
            id = comment.id,
            content = comment.content,
            userInfo = commentUserDto(
                nickname = comment.user.nickname,
                profileImg = comment.user.profileImageUrl ?: ""
            ),
            postId = comment.post.id,
            imgUrl = comment.imgurl,
            createAt = comment.createdAt
        )
    }
}
