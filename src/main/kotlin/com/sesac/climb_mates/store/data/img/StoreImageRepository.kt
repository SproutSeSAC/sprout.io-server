package com.sesac.climb_mates.store.data.img

import org.springframework.data.jpa.repository.JpaRepository

interface StoreImageRepository:JpaRepository<StoreImage, Long> {
    fun findByStoreId(storeId: Long): List<StoreImage>
}