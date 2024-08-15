package com.sesac.climb_mates.account.data.google

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface GoogleAccountRepository:JpaRepository<GoogleAccount, String> {
    fun findBySub(sub:String): Optional<GoogleAccount>
    fun findByEmail(email:String): Optional<GoogleAccount>
}