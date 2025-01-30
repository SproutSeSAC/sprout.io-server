package io.sprout.api.post.repository

import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findByPostType(postType: PostType): List<PostEntity>
    fun findAllByClientId(clientId: Long): List<PostEntity>

    @Query("""
        SELECT p.linkedId
        FROM PostEntity p
        JOIN NoticeEntity n ON n.id = p.linkedId
        JOIN NoticeSessionEntity ns ON ns.notice.id = n.id
        JOIN NoticeParticipantEntity np ON np.noticeSession.id = ns.id
        WHERE p.postType = 'NOTICE' AND np.user.id = :userId
    """)
    fun findNoticeIdsByUserIdFromParticipant(@Param("userId") userId: Long): List<Long>

    @Query("""
        SELECT p
        FROM PostEntity p
        JOIN NoticeEntity n ON n.id = p.linkedId
        JOIN NoticeSessionEntity ns ON ns.notice.id = n.id
        JOIN NoticeParticipantEntity np ON np.noticeSession.id = ns.id
        WHERE p.postType = 'NOTICE' AND np.user.id = :userId
    """)
    fun findNoticesByUserIdFromParticipant(@Param("userId") userId: Long): List<PostEntity>

    fun findByLinkedIdAndPostType(linkedId: Long, postType: PostType): PostEntity?
}