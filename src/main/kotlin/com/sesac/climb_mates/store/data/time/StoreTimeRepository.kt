package com.sesac.climb_mates.store.data.time

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface StoreTimeRepository:JpaRepository<StoreTime, Long>{
    fun findByStoreId(storeId: Long): Optional<StoreTime>
}