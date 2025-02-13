package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_memo")
class UserMemo(
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity,

    @ManyToOne
    @JoinColumn(name = "target_user_id")
    var targetUser: UserEntity,

    var content: String

): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}