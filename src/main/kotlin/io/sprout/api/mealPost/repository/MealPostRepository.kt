package io.sprout.api.mealPost.repository

import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.model.entities.MealPostEntity
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

//    // TODO: 총 인원 및 현재 참여자 수 내려주기,
//    // 삭제 필요
//    @Query(
//        nativeQuery = true,
//        value = "SELECT " +
//                "mp.id, mp.title, mp.appointment_time, mp.member_count, mp.meeting_place, mp_participation.ordinal_number " +
//                "FROM meal_post AS mp " +
//                "LEFT JOIN meal_post_participation AS mp_participation ON mp.id = mp_participation.meal_post_id " +
//                "WHERE mp.meal_post_status = 'ACTIVE' "
//    )
//    fun findMealPostList(pageable: Pageable): Page<MealPostProjection>
}