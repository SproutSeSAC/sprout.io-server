package io.sprout.api.post.entities

import io.sprout.api.comment.entity.CommentEntity
import io.sprout.api.notice.repository.NoticeRepository
import io.sprout.api.project.repository.ProjectRepository
import jakarta.persistence.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "post")
@Configurable
class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "client_id", nullable = false)
    var clientId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var postType: PostType,

    @Column(name = "linked_id", nullable = false)
    var linkedId: Long,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val comments: MutableList<CommentEntity> = mutableListOf()
) {
    @Transient
    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Transient
    @Autowired
    private lateinit var noticeRepository: NoticeRepository

    @PreRemove // 이건 차후 삭제하면서 서비스에 병합시키는게 좋아 보입니다.
    fun deleteLinkedEntity() {
        try {
            when (postType) {
                PostType.PROJECT -> linkedId?.let { projectRepository.deleteById(it) }
                PostType.NOTICE -> linkedId?.let { noticeRepository.deleteById(it) }
            }
        } catch (e: Exception) {
            println("삭제 실패 : ${e.message}")
        }
    }
}

enum class PostType {
    NOTICE, // 공지사항
    PROJECT // 모집글
}
