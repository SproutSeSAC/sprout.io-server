package io.sprout.api.post.entities

import io.sprout.api.comment.entity.CommentEntity
import io.sprout.api.mealPost.service.MealPostService
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
}

enum class PostType {
    NOTICE, // 공지사항
    PROJECT, // 모집글
    MEAL // 한끼팟
}
