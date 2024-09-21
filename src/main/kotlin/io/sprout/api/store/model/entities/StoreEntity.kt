package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store")
class StoreEntity(

    @Column(nullable = false, length = 100)
    var name: String, // 가게명

    @Column(nullable = false, length = 100)
    var address: String, // 주소

    @Column(nullable = false, length = 20)
    var contact: String, // 연락처

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false, length = 10)
    var foodType: FoodType, // 요리 타입,

    @Column(name = "working_day", nullable = true, length = 500)
    var workingDay: String, // 영업일 및 영업 시간

    @Column(name = "break_time", nullable = false, length = 50)
    var breakTime: String, // 브레이크 시간

    @Column(nullable = false, length = 10)
    var holiday: String, // 휴무일 (MON,TUE,WED,THU,FRI,SAT,SUN)

    @Column(name = "is_voucher", nullable = false)
    var isVoucher: Boolean, // 식대지원 유무

    @Column(name = "is_zeropay", nullable = false)
    var isZeropay: Boolean, // 제로페이 사용가능 유무

    @Column(name = "walk_time", nullable = false)
    var walkTime: Int, // 도보 시간

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "map_url", nullable = true, length = 500)
    var mapUrl: String? = null // 네이버 지도 URL

    @Column(name = "map_schema_url", nullable = true, length = 500)
    var mapSchemaUrl: String? = null // 네이버 지도 Schema URL

    @Column(name = "instagram_url", nullable = true, length = 500)
    var instagramUrl: String? = null // 인스타 URL

    @OneToMany(mappedBy = "store", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var storeStoryList: MutableSet<StoreStoryEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "store", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var storeMenuList: MutableSet<StoreMenuEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "store", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var storeImageList: MutableSet<StoreImageEntity> = LinkedHashSet()

}

enum class FoodType {
    KOREAN, CHINESE, JAPANESE, WESTERN, ASIAN, SNACK, CAFE
}