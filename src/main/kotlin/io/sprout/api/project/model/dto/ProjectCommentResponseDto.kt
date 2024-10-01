package io.sprout.api.project.model.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ProjectCommentResponseDto @QueryProjection constructor(
    val id : Long,
    val content : String,
    val createdAt : LocalDateTime,
    val writer : String,
    val projectId : Long,
)