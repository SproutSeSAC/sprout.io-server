package io.sprout.api.project.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.querydsl.core.annotations.QueryProjection
import io.sprout.api.project.model.entities.ContactMethod
import io.sprout.api.project.model.entities.MeetingType
import io.sprout.api.project.model.entities.PType
import io.sprout.api.project.model.entities.ProjectStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class ProjectDetailResponseDto @QueryProjection constructor(
    val id: Long,
    val title: String,
    val writerId : Long,
    val writerNickName: String, // 작성자의 이름
    val description: String,
    val pType: PType, // 프로젝트 유형 (PROJECT, STUDY)
    val recruitmentCount: Int, // 모집 인원
    val contactMethod: ContactMethod, // 연락 방법
    val recruitmentStart: LocalDate, // 모집 시작일
    val recruitmentEnd: LocalDate, // 모집 종료일
    val viewCount: Int, // 조회수
    val projectStatus: ProjectStatus, // 프로젝트 상태 (ACTIVE, INACTIVE, END)
    val meetingType: MeetingType, // 미팅 방식 (ONLINE, OFFLINE, HYBRID)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    val createdAt: LocalDateTime,
    val imgUrl : String?,
    val contactDetail : String?,
    val positionNames: List<String>, // 프로젝트의 포지션들
){
    var isScraped: Boolean = false
}