package io.sprout.api.mealPost.repository

import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.model.entities.MealPostEntity
import io.sprout.api.store.model.entities.StoreEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MealPostRepository: JpaRepository<MealPostEntity, Long>, MealPostRepositoryCustom {

    @EntityGraph(attributePaths = ["mealPostParticipationList", "mealPostParticipationList.user"])
    fun findWithParticipationUserById(mealPostId: Long): MealPostEntity?

}