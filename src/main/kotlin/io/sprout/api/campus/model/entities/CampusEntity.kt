package io.sprout.api.campus.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.store.model.entities.StoreEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "campus")
class CampusEntity(

    @Column(nullable = false, length = 50)
    var name: String, // 캠퍼스명

    @Column(nullable = false, length = 200)
    var address: String, // 캠퍼스 주소

    val longitude: String,

    val latitude: String,

    val naverPlaceId: String,

): BaseEntity()  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(mappedBy = "campus", fetch = FetchType.LAZY)
    var storeList: MutableSet<StoreEntity> = LinkedHashSet()

    constructor(id: Long) : this("", "", "", "", "") {
        this.id = id
    }
}