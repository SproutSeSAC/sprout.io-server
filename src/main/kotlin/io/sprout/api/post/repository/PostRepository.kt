package io.sprout.api.post.repository

import io.sprout.api.post.entity.PostEntity
import io.sprout.api.post.entity.PostType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findByPostType(postType: PostType): List<PostEntity>
    fun findByPostTypeAndReferenceId(postType: PostType, referenceId: Long): PostEntity?

}