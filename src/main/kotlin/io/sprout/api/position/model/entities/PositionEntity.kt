package io.sprout.api.position.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "position_tbl")
class PositionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

){
    @Column(nullable = false)
    val name: String = ""  // 예: 프론트엔드, 백엔드, 디자이너 등
}