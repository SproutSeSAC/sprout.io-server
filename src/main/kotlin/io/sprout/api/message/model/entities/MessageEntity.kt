package io.sprout.api.message.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "message")
class MessageEntity(

    // 메세지 수신 유저
    @Column(name = "receiver_id",nullable = false)
    var receiverId: Long,

    // 메세지 내용
    @Column(nullable = false)
    var contents: String,

    // 읽음 상태
    @Column(name = "read_status", nullable = false)
    var readStatus: Boolean,

    // 메세지 보낸 유저
    @Column(name = "sender_name", nullable = false)
    var senderName: String,

    // 메세지 links
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, length = 4000)
    var links: String? = null


): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

}