package io.sprout.api.project.model.dto

import io.sprout.api.project.model.entities.PType

data class ProjectFilterRequest(
    val techStack: List<Long>?,    // 기술 스택 ID 리스트
    val position: List<Long>?,         // 포지션 (예: '백엔드', '프론트엔드')
    val meetingType: String?,       // 진행 방식 (예: '온라인', '오프라인')
    val page: Int = 1,
    val size: Int = 20,
    val onlyScraped: Boolean = false,
    val pType: PType? ,
    val sort: String = "latest",
    val keyWord: String?
)
