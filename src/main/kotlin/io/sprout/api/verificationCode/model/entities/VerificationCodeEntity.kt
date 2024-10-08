package io.sprout.api.verificationCode.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "verification_code")
class VerificationCodeEntity(

    @Column(nullable = false)
    val code: String, // 확인 코드

    @Column(nullable = false)
    var useCount: Int = 0 // 사용 횟수

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}