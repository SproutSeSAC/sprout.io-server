package io.sprout.api.project.model.dto

data class ProjectFilterRequest(
    val techStack: List<Long>?,    // 기술 스택 ID 리스트
    val position: List<Long>?,         // 포지션 (예: '백엔드', '프론트엔드')
    val meetingType: String?       // 진행 방식 (예: '온라인', '오프라인')
)
