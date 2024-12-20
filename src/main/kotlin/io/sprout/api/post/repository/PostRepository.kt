package io.sprout.api.post.repository

import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findByPostType(postType: PostType): List<PostEntity>
    fun findAllByClientId(clientId: Long): List<PostEntity>
}