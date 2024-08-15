package com.sesac.climb_mates.account.data

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AccountRepository:JpaRepository<Account, Long> {
    fun findByUsername(username:String):Optional<Account>
    fun findByEmail(email:String):Optional<Account>
    fun findByNickname(nickname:String):Optional<Account>
}