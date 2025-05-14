package io.sprout.api.post.repository

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PostRepository : JpaRepository<PostEntity, Long> {
    fun findByPostType(postType: PostType): List<PostEntity>
    fun findAllByClientId(clientId: Long): List<PostEntity>
    fun findAllByClientId(clientId: Long, pageable: Pageable): Page<PostEntity>

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
        SELECT np
        FROM NoticeParticipantEntity np
        JOIN np.noticeSession ns
        JOIN ns.notice n
        WHERE np.user.id = :userId
    """)
    fun findNoticesByUserIdFromParticipant(@Param("userId") userId: Long): List<NoticeParticipantEntity>

    @Query("""
    SELECT po
    FROM PostEntity po
    WHERE po.linkedId = :projectId
    AND po.postType = :postType
""")
    fun findLinkedIdByDataId(@Param("projectId") projectId: Long, @Param("postType") postType: PostType): PostEntity

    
    fun findByLinkedIdAndPostType(linkedId: Long, postType: PostType): PostEntity?

    @Modifying
    @Query("""
        UPDATE PostEntity po
        SET po.clientId = :nextId
        WHERE  po.clientId = :prevId
    """)
    fun updateClientId(prevId: Long, nextId: Long)
}