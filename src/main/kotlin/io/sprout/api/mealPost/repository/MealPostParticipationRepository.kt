package io.sprout.api.mealPost.repository

import io.sprout.api.mealPost.model.entities.MealPostParticipationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MealPostParticipationRepository: JpaRepository<MealPostParticipationEntity, Long> {

    @Query("SELECT count(mpp.id) > 0 " +
            "FROM MealPostParticipationEntity mpp " +
            "WHERE mpp.mealPost.id = :mealPostId AND mpp.user.id = :userId AND mpp.ordinalNumber = 1")
    fun isOwner(mealPostId: Long, userId: Long): Boolean



}