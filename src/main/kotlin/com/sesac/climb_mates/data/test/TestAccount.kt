package com.sesac.climb_mates.data.test

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name="test_account")
data class TestAccount(
    @Id
    @Column(name="email", unique = true, nullable = false)
    val email:String,

    @Column(name="sub", unique = true, nullable = false)
    val sub:String,
    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()
)
