package com.sesac.climb_mates.data.account.google

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name="google_account")
data class GoogleAccount(
    @Id
    @Column(name="sub", unique = true, nullable = false)
    val sub:String,

    @Column(name="email", unique = true, nullable = false)
    val email:String,
    @Column(name="picture", nullable = true)
    val picture:String,
    @Column(name="name", nullable = true)
    val name:String,


    @Column(name = "created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()
)
