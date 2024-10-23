package io.sprout.api.notice.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.notice.model.dto.NoticeResponseDto
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "notice")
class NoticeEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,  // 공지사항 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    val writer: UserEntity,  // 공지사항 작성자

    @Column(name = "start_date" , nullable = false)
    var startDate: LocalDate,  // 공지 시작일
    @Column(name = "end_date")
    var endDate: LocalDate? = null,  // 공지 종료일 (선택 사항)

    @Enumerated(EnumType.STRING)
    var status: NoticeStatus? = NoticeStatus.ACTIVE,  // 공지 상태 (활성, 비활성 등)

    @Enumerated(EnumType.STRING)
    var noticeType: NoticeType,  // 공지 유형 (특강, 취업, 매칭데이, 일반)

    @Column(nullable = false)
    var viewCount: Int = 0,

    ) : BaseEntity(){
    fun toDto(): NoticeResponseDto {
        return NoticeResponseDto(
            id = this.id,
            title = this.title,
            content = this.content,
            writerName = this.writer.name ?: "익명의 사용자",
            profileUrl = this.writer.profileImageUrl ?: "",
            startDate = this.startDate,
            endDate = this.endDate ?: LocalDate.now(),
            status = this.status ?: NoticeStatus.ACTIVE,
            noticeType = this.noticeType,
            createdDateTime = this.createdAt,
            modifiedDateTime = this.updatedAt,
            viewCount = this.viewCount,
            isScraped = false,
        )
    }
}

enum class NoticeStatus {
    ACTIVE,  // 현재 활성화된 공지사항
    INACTIVE,  // 비활성화된 공지사항
}

enum class NoticeType {
    SPECIAL_LECTURE,  // 특강 관련 공지
    EMPLOYMENT,       // 취업 관련 공지
    MATCHING_DAY,     // 매칭데이 관련 공지
    GENERAL           // 일반 공지
}