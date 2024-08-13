package com.sesac.climb_mates.grouping.data

import com.sesac.climb_mates.account.data.Account
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="grouping_applicant")
data class GroupingApplicant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    var grouping: Grouping,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    var account: Account,

    @Column(name="created_date", nullable = false)
    val createdDate: LocalDateTime = LocalDateTime.now()
)