package io.sprout.api.mealPost.repository

import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.store.model.dto.StoreDto
import io.sprout.api.store.model.dto.StoreProjectionDto
import org.springframework.data.domain.Pageable

interface MealPostRepositoryCustom {

    fun findMealPostList(pageable: Pageable, userId: Long): List<MealPostProjection>

}
