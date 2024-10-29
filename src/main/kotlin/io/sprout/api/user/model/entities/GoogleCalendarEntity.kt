package io.sprout.api.user.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "google_calendar")
class GoogleCalendarEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0,

    @Column(name = "calendar_id",nullable = false, unique = true)
    var calendarId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = false)
    var user: UserEntity,

    @Column(nullable = false, unique = false)
    var courseId: Long
)