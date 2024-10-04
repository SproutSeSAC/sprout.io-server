package io.sprout.api.project.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ProjectCommentResponseDto @QueryProjection constructor(
    val id : Long,
    val content : String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    val createdAt : LocalDateTime,
    val writer : String,
    val projectId : Long,
    val imgUrl : String,
)