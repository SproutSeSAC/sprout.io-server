package io.sprout.api.verificationCode.repository

import io.sprout.api.verificationCode.model.entities.VerificationCodeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationCodeRepository: JpaRepository<VerificationCodeEntity, Long> {
}