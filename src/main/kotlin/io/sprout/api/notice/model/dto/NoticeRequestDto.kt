package io.sprout.api.notice.model.dto

import io.sprout.api.notice.model.entities.NoticeEntity
import io.sprout.api.notice.model.entities.NoticeStatus
import io.sprout.api.notice.model.entities.NoticeType
import io.sprout.api.user.model.entities.UserEntity
import java.time.LocalDateTime

data class NoticeRequestDto(
    val title: String,
    val content: String,
    val writerId: Long,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val noticeType: NoticeType
){
    fun toEntity(writer: UserEntity): NoticeEntity {
        return NoticeEntity(
            id = 0L,  // 새로운 엔티티 생성 시 ID는 0으로 설정
            title = this.title,
            content = this.content,
            writer = writer,
            startDate = this.startDate,
            endDate = this.endDate,
            status = NoticeStatus.ACTIVE,
            noticeType = this.noticeType
        )
    }
}