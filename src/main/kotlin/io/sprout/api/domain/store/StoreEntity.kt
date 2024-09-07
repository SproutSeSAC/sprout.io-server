package io.sprout.api.domain.store

import io.sprout.api.domain.base.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store")
class StoreEntity(

    @Column(nullable = false, length = 100)
    var storeName: String, // 가게명

    @Column(nullable = false, length = 20)
    var contact: String, // 연락처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var foodType: FoodType, // 요리 타입,

    @Column(nullable = true, length = 500)
    var workingDay: String, // 영업일 및 영업 시간

    @Column(nullable = false, length = 50)
    var breakTime: String, // 브레이크 시간

    @Column(nullable = false, length = 10)
    var holiday: String, // 휴무일 (MON,TUE,WED,THU,FRI,SAT,SUN)

    @Column(nullable = false)
    var isVoucher: Boolean, // 식대지원 유무

    @Column(nullable = false)
    var isZeroPay: Boolean, // 제로페이 사용가능 유무

    @Column(nullable = false)
    var walkTime: Int, // 도보 시간

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = true, length = 500)
    var naverMapUrl: String? = null // 네이버 지도 URL

    @Column(nullable = true, length = 500)
    var naverMapSchemaUrl: String? = null // 네이버 지도 Schema URL

    @Column(nullable = true, length = 500)
    var instagramUrl: String? = null // 인스타 URL

}

enum class FoodType {
    KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE
}