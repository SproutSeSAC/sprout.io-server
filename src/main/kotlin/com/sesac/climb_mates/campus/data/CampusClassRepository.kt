package com.sesac.climb_mates.campus.data

import org.springframework.data.jpa.repository.JpaRepository

interface CampusClassRepository:JpaRepository<CampusClass, Long> {
    fun findByCampusName(campusName:String):List<CampusClass>
}