package io.sprout.api.common.model.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(value = [AuditingEntityListener::class])
class BaseEntity {

    @CreatedDate
    @Comment("생성 시간")
    @Column(name = "created_date_time", nullable = false, updatable = false)
    var createdAt: LocalDateTime =  LocalDateTime.MIN

    @LastModifiedDate
    @Comment("수정 시간")
    @Column(name = "modified_date_time", nullable = false)
    var updatedAt: LocalDateTime =  LocalDateTime.MIN

    @PrePersist
    fun formattingBeforeCreateDate() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val customLocalDateTime = LocalDateTime.now().format(formatter)
        createdAt = LocalDateTime.parse(customLocalDateTime, formatter)
        updatedAt = LocalDateTime.parse(customLocalDateTime, formatter)
    }

    @PreUpdate
    fun formattingBeforeModifiedDate() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val customLocalDateTime = LocalDateTime.now().format(formatter)
        updatedAt = LocalDateTime.parse(customLocalDateTime, formatter)
    }
}