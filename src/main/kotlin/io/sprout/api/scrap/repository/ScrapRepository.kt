package io.sprout.api.scrap.repository

import io.sprout.api.scrap.entity.ScrapEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScrapRepository : JpaRepository<ScrapEntity, Long> {
    fun findByUserId(userId: Long): List<ScrapEntity>
    fun findByUserIdAndPostId(userId: Long, postId: Long): ScrapEntity?
    fun deleteAllByPostId(postId: Long)
    @Query(value = "SELECT post.linked_id from post inner join scrap on post.id = scrap.post_id WHERE scrap.user_id = :userId", nativeQuery = true)
    fun findLinkedIdByUserId(userId: Long): List<Long?>
}