package io.sprout.api.post.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.sprout.api.notice.model.dto.NoticeRequestDto
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = NoticeRequestDto::class, name = "notice"),
    JsonSubTypes.Type(value = ProjectRecruitmentRequestDto::class, name = "project")
)
sealed class PostRequestDto
