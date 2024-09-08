package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store_image")
class StoreImageEntity(

    @Column(nullable = false, length = 500)
    var path: String, // 이미지 경로

    @Column(nullable = false)
    var size: Int, // 이미지 사이즈

    @ManyToOne
    var store: StoreEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
