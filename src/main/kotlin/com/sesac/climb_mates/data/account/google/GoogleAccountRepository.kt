package com.sesac.climb_mates.data.account.google

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import javax.swing.text.html.Option

interface GoogleAccountRepository:JpaRepository<GoogleAccount, String> {
    fun findBySub(sub:String): Optional<GoogleAccount>
    fun findByEmail(email:String): Optional<GoogleAccount>
}