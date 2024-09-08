package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store_story")
class StoreStoryEntity(

    @Column(nullable = false, length = 500)
    var path: String, // 스토리 경로

    @Column(nullable = false)
    var size: Int, // 사이즈

    @ManyToOne
    var store: StoreEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
