package io.sprout.api.notice.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class NoticeJoinRequestListDto @QueryProjection constructor (
    val id : Long,
    val userName : String,
    val userNickname : String,
    val userId : Long,
    val noticeId : Long,
    val noticeSubTitle : String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    val createdAt: LocalDateTime,
)