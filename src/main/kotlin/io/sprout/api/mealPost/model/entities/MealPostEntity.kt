package io.sprout.api.mealPost.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.store.model.entities.StoreEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "meal_post")
class MealPostEntity(

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(nullable = false)
    val startDateTime: LocalDateTime,

    @Column(nullable = false)
    val recruitmentCount: Int,

    @Column(nullable = false, length = 100)
    val meetingPlace: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_post_status", nullable = false, length = 10)
    val mealPostStatus: MealPostStatus,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    var store: StoreEntity

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @OneToMany(mappedBy = "mealPost", cascade = [CascadeType.ALL])
    val mealPostParticipationList: List<MealPostParticipationEntity> = listOf()

}

enum class MealPostStatus {
    ACTIVE, INACTIVE, END
}