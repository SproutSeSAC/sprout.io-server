package com.sesac.climb_mates.data.test

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TestAccountRepository:JpaRepository<TestAccount, String> {
    fun findByEmail(email:String): Optional<TestAccount>
}