package com.sesac.climb_mates.grouping.data.review

import org.springframework.data.jpa.repository.JpaRepository

interface GroupingReviewRepository:JpaRepository<GroupingReview, Long> {
    fun findByGroupingId(groupingId: Long): List<GroupingReview>
}