package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "store_review")
class StoreReviewEntity(

    @Column(nullable = true, length = 500)
    var review: String?, // 리뷰 내용

    @Column(nullable = true)
    var rating: Int?, // 별점

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: UserEntity, // 유저 정보

    @ManyToOne
    var store: StoreEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
