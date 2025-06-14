package io.sprout.api.post.repository

import io.sprout.api.notice.model.entities.NoticeParticipantEntity
import io.sprout.api.post.entities.PostEntity
import io.sprout.api.post.entities.PostType
import io.sprout.api.project.model.entities.PType
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
    fun findAllByClientIdAndPostTypeIn(clientId: Long, postType: List<PostType>, pageable: Pageable): Page<PostEntity>

    @Query("""
    SELECT a.*, b.pType FROM post a
    LEFT JOIN project b ON a.linked_id = b.id AND a.postType = 'PROJECT'
    WHERE a.client_id = :clientId
      AND CASE 
          WHEN a.postType = 'PROJECT' THEN b.pType
          ELSE a.postType
        END IN :postTypes
    """, nativeQuery = true)
    fun testSQL(
        @Param("clientId") clientId: Long,
        @Param("postTypes") postTypes: List<String>,
        pageable: Pageable
    ): Page<PostEntity>

    @Query("""
    SELECT a FROM PostEntity a
    JOIN ProjectEntity b ON a.linkedId = b.id
    WHERE a.clientId = :clientId
      AND a.postType IN :postTypes
      AND b.pType IN :projectTypes
""")
    fun findProjectPostsByPostType(
        @Param("clientId") clientId: Long,
        @Param("postTypes") postTypes: List<PostType>,
        @Param("projectTypes") projectTypes: List<PType>,
        pageable: Pageable
    ): Page<PostEntity>

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
    fun findLinkedIdByDataId(@Param("projectId") projectId: Long, @Param("postType") postType: PostType): PostEntity?

    
    fun findByLinkedIdAndPostType(linkedId: Long, postType: PostType): PostEntity?

    @Modifying
    @Query("""
        UPDATE PostEntity po
        SET po.clientId = :nextId
        WHERE  po.clientId = :prevId
    """)
    fun updateClientId(prevId: Long, nextId: Long)
}