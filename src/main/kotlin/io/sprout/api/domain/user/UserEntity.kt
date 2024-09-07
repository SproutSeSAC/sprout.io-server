package io.sprout.api.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserEntity(

    @Column(nullable = false, length = 100)
    var name: String, // 유저명

    @Column(unique = true, nullable = false, length = 100)
    var email: String, // 이메일

    var avatarImgUrl: String?, // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: RoleType, // 유저 권한(= 유형)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var status: UserStatus, // 유저 상태

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 700)
    var refreshToken: String? = null // 리프레시 토큰

    var lastLoginDateTime: LocalDateTime? = null // 마지막 로그인 시간

    var marketingConsent: Boolean = false // 마케팅 약관 동의여부

}

enum class UserStatus {
    ACTIVE, INACTIVE, SLEEP, LEAVE
}

enum class RoleType {
    ADMIN, TRAINEE, MANAGER
}