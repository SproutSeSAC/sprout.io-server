package io.sprout.api.mealPost.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

@Entity
@Table(name = "meal_post")
class MealPostEntity(

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(name = "appointment_time", nullable = false)
    val appointmentTime: LocalDateTime,

    @Column(name = "member_count", nullable = false)
    val memberCount: Int,

    @Column(name = "meeting_place", nullable = false, length = 100)
    val meetingPlace: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_post_status", nullable = false, length = 10)
    val mealPostStatus: MealPostStatus,

    @Column(name = "store_name", length = 100)
    var storeName: String,

    // mealPost 첫번째 사람으로 글쓴이 판별하자

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    // orphanRemoval = true, mealPost 컬렉션 내 mealPostParticipation 삭제 시, 자동 삭제 처리
    @OneToMany(mappedBy = "mealPost", cascade = [CascadeType.ALL], orphanRemoval = true)
    val mealPostParticipationList: MutableSet<MealPostParticipationEntity> = mutableSetOf()

    fun countJoinMember(): Int {
        return this.mealPostParticipationList.size
    }

}

enum class MealPostStatus {
    ACTIVE, INACTIVE, FINISHED
}