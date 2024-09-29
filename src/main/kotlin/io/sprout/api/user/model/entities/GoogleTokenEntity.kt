package io.sprout.api.user.model.entities

import jakarta.persistence.Entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "google_token")
class GoogleTokenEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: UserEntity,  // UserEntity의 id와 연결

    @Column(name = "access_token", nullable = false, length = 700)
    var accessToken: String,  // 액세스 토큰

    @Column(name = "refresh_token", nullable = true, length = 700)
    var refreshToken: String?,  // 리프레시 토큰

    @Column(name = "access_token_expiration", nullable = false)
    var accessTokenExpiration: Instant

) {
    fun updateAccessTokenAndRefreshToken(accessToken: String, refreshToken: String?, expiresIn: Int) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.accessTokenExpiration = Instant.now().plusSeconds(expiresIn.toLong())
    }

    // 토큰 갱신 메서드
    fun updateAccessToken(newAccessToken: String, expiresIn: Int) {
        this.accessToken = newAccessToken
        this.accessTokenExpiration = Instant.now().plusSeconds(expiresIn.toLong())
    }
}
