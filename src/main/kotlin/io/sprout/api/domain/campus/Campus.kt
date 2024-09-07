package io.sprout.api.domain.campus

import io.sprout.api.domain.base.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "campus")
class Campus(

    @Column(nullable = false, length = 50)
    var name: String, // 캠퍼스명

    @Column(nullable = false, length = 200)
    var address: String, // 캠퍼스 주소

): BaseEntity()  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}