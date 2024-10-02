package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store_menu")
class StoreMenuEntity(

    @Column(nullable = false, length = 50)
    var name: String, // 메뉴 이름

    @Column(nullable = false)
    var price: Int, // 메뉴 가격

    @ManyToOne
    var store: StoreEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "image_url", nullable = true, length = 500)
    var imageUrl: String? = null // 메뉴 이미지 경로

}
