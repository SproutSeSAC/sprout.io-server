package io.sprout.api.notice.model.dto

import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.notice.model.entities.*
import io.sprout.api.user.model.entities.UserEntity
import jakarta.validation.constraints.NotBlank

/**
 * 일반 공지사항 생성 요청 DTO
 * (일반공지, 취업, 기타 타입)
 */
data class NormalNoticeRequestDto(
    val targetCourseIdList: Set<Long>,

    @field:NotBlank(message = "제목은 비어있으면 안됩니다.")
    val title: String,
    @field:NotBlank(message = "내용은 비어있으면 안됩니다.")
    val content: String,

    val noticeType: NoticeType,
){
    fun toEntity(userId: Long): NoticeEntity {
        val noticeEntity = NoticeEntity(
            title = this.title,
            content = this.content,
            user = UserEntity(userId),
            status = NoticeStatus.ACTIVE,
            noticeType = this.noticeType,
            meetingType = NoticeMeetingType.NONE,
            viewCount = 0,
        )

        noticeEntity.targetCourses.plusAssign(targetCourseIdList.map { NoticeTargetCourseEntity(
            notice = noticeEntity,
            course = CourseEntity(it)
        ) }
            .toMutableList())

        return noticeEntity
    }
}