package io.sprout.api.user.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserEntity(

    @Column(nullable = false, length = 50)
    var nickname: String, // 닉네임

    @Column(unique = true, nullable = false, length = 50)
    var email: String, // 이메일

    @Column(name = "profile_image_url", nullable = true, length = 100)
    var profileImageUrl: String?, // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: RoleType, // 유저 권한(= 유형)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var status: UserStatus, // 유저 상태

    @ManyToOne
    var course: CourseEntity,

    @Column(name = "is_essential")
    var isEssential: Boolean

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 20)
    var name: String? = null // 유저명

    @Column(name = "refresh_token", length = 700)
    var refreshToken: String? = null // 리프레시 토큰

    @Column(name = "last_login_date_time")
    var lastLoginDateTime: LocalDateTime? = null // 마지막 로그인 시간

    @Column(name = "marketing_consent")
    var marketingConsent: Boolean = false // 마케팅 약관 동의여부

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userJobList: MutableSet<UserJobEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userDomainList: MutableSet<UserDomainEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var userTechStackList: MutableSet<UserTechStackEntity> = LinkedHashSet()

    fun addRefreshToken(refreshToken: String){
        this.refreshToken = refreshToken
    }

}

enum class UserStatus {
    ACTIVE, INACTIVE, SLEEP, LEAVE
}

enum class RoleType {
    ADMIN, TRAINEE, PRE_TRAINEE, CAMPUS_MANAGER, EDU_MANAGER, JOB_COORDINATOR,
}