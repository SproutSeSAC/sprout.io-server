package com.sesac.climb_mates.account.data.google

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface GoogleAccountInfoRepository:JpaRepository<GoogleAccountInfo, String> {
    fun findByGoogleAccountSub(email:String): Optional<GoogleAccountInfo>
}