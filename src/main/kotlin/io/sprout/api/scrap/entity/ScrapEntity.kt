package io.sprout.api.scrap.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "scrap")
data class ScrapEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "user_id", nullable = false)
        val userId: Long,

        @Column(name = "post_id", nullable = false)
        val postId: Long
)