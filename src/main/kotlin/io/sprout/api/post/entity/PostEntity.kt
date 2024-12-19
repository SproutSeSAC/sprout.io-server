package io.sprout.api.post.entities

import io.sprout.api.notice.repository.NoticeRepository
import io.sprout.api.project.repository.ProjectRepository
import jakarta.persistence.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable

@Entity
@Table(name = "post")
@Configurable
class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var postType: PostType,

    @Column(name = "linked_id", nullable = true)
    var linkedId: Long? = null
) {
    @Transient
    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Transient
    @Autowired
    private lateinit var noticeRepository: NoticeRepository

    @PreRemove
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
