package io.sprout.api.notice.event

import io.sprout.api.notice.model.dto.NoticeNotification
import io.sprout.api.notice.service.NoticeEventService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ParticipationEventHandler(private val noticeEventService: NoticeEventService) {

    @EventListener
    fun handleParticipationRequest(event: ParticipationRequestEvent) {
        val notification = NoticeNotification(
            noticeId = event.noticeId,
            userId = event.userId,
            userName = event.userName,
            userNickName = event.userNickName,
            message = "User ${event.userName} (${event.userNickName}) requested to join Notice ${event.noticeTitle}"
        )
        noticeEventService.sendToAdmin(notification)
    }

    @EventListener
    fun handleParticipationResponse(event: ParticipationResponseEvent) {
        val message = if (event.accepted) "Your participation request for ${event.noticeTitle} was accepted." else "Your participation request for ${event.noticeTitle} was rejected."
        val notification = NoticeNotification(
            noticeId = event.noticeId,
            userId = event.userId,
            userName = event.userName,
            userNickName = event.userNickName,
            message = message
        )
        noticeEventService.sendToUser(event.userId, notification)
    }
}

data class ParticipationRequestEvent(
    val noticeId: Long,
    val userId: Long,
    val userName: String,
    val userNickName: String,
    val noticeTitle: String
)

data class ParticipationResponseEvent(
    val noticeId: Long,
    val userId: Long,
    val userName: String,
    val userNickName: String,
    val noticeTitle: String,
    val accepted: Boolean
)
