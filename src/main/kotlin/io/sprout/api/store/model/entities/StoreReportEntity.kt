package io.sprout.api.store.model.entities

import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*


@Entity
@Table(name = "store_report")
class StoreReportEntity(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    var store: StoreEntity?,

    var type: String,

    var targetStoreName: String?,

    var content: String

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}
