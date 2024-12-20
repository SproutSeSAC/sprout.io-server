package io.sprout.api.comment.repository

import io.sprout.api.comment.entity.CommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<CommentEntity, Long> {
    fun findByPostId(postId: Long): List<CommentEntity>
    fun findAllByUser_Id(userId: Long): List<CommentEntity>
}
