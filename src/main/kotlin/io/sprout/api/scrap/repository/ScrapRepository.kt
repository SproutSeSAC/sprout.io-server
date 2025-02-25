package io.sprout.api.scrap.repository

import io.sprout.api.scrap.entity.ScrapEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ScrapRepository : JpaRepository<ScrapEntity, Long> {
    fun findByUserId(userId: Long): List<ScrapEntity>
    fun findByUserIdAndPostId(userId: Long, postId: Long): ScrapEntity?
    fun deleteAllByPostId(postId: Long)
}