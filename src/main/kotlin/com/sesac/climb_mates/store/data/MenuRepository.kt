package com.sesac.climb_mates.store.data

import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository:JpaRepository<Menu, Long> {
    fun findByStoreId(storeId:Long): List<Menu>
}