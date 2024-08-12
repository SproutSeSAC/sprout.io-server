package com.sesac.climb_mates.data.account.google

import jakarta.persistence.*

@Entity
@Table(name="google_account_info")
data class GoogleAccountInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "google_account_sub", nullable = false)
    var googleAccount: GoogleAccount,

    @Column(name="nickname", nullable = false, unique = true)
    var nickname:String,

    @Column(name="birth", nullable = false)
    val birth:String,
    @Column(name="gender", nullable = false)
    val gender:Int,

    @Column(name="campus", nullable = true)
    val campus:String,
    @Column(name="education", nullable = true)
    val education:String
)
