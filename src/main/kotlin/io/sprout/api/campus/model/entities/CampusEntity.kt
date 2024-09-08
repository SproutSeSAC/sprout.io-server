package io.sprout.api.campus.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "campus")
class CampusEntity(

    @Column(nullable = false, length = 50)
    var name: String, // 캠퍼스명

    @Column(nullable = false, length = 200)
    var address: String, // 캠퍼스 주소

): BaseEntity()  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}