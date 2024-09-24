package io.sprout.api.project.model.entities

import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.*


@Entity
@Table(name = "scraped_project")
class ScrapedProjectEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    var project: ProjectEntity,
)
