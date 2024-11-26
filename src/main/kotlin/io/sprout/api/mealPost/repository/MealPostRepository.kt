package io.sprout.api.mealPost.repository

import io.sprout.api.mealPost.model.entities.MealPostEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MealPostRepository: JpaRepository<MealPostEntity, Long>, MealPostRepositoryCustom {

    @EntityGraph(attributePaths = ["mealPostParticipationList", "mealPostParticipationList.user"])
    fun findWithParticipationUserById(mealPostId: Long): MealPostEntity?


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT mealpost FROM MealPostEntity mealpost WHERE mealpost.id = :id")
    fun findByIdWithLock(id: Long): MealPostEntity?
}