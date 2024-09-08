package io.sprout.api.common.model.entities

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDateTime

@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(value = [AuditingEntityListener::class])
class BaseEntity {

    @CreatedDate
    @Column(name = "created_date_time")
    @Comment("생성 시간")
    @ColumnDefault("now()")
    var createdDateTime: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(name = "modified_date_time")
    @Comment("수정 시간")
    @ColumnDefault("now()")
    var modifiedDateTime: LocalDateTime = LocalDateTime.now()
}