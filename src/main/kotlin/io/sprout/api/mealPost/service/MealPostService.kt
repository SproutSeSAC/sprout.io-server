package io.sprout.api.mealPost.service

import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.repository.MealPostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MealPostService(
    private val mealPostRepository: MealPostRepository
) {
    fun getMealPostList(pageable: Pageable): Page<MealPostProjection> {
        val page: Int = if (pageable.pageNumber == 0) 0 else pageable.pageNumber - 1
        val pageable = PageRequest.of(page, pageable.pageSize, Sort.by(Sort.Order.desc("created_date_time")))
        return mealPostRepository.findMealPostList(pageable)
    }
}