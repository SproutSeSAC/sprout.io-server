package io.sprout.api.post.repository

import io.sprout.api.post.entities.PostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<PostEntity, Long>
