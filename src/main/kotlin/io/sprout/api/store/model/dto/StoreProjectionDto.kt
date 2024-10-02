package io.sprout.api.store.model.dto

import io.sprout.api.store.model.entities.FoodType

class StoreProjectionDto{

    data class StoreInfoDto(
        val id: Long,
        val name: String,
        val address: String,
        val contact: String,
        val foodType: FoodType,
        val workingDay: String,
        val breakTime: String,
        val holiday: String,
        val isZeropay: Boolean,
        val walkTime: Int,
        val isOverPerson: Boolean,
        val storeImageList: List<StoreImageDto>,
        val storeMenuList: List<StoreMenuDto>
    )

    data class StoreImageDto(
        val id: Long?,
        val path: String?
    )

    data class StoreMenuDto(
        val id: Long?,
        val name: String?,
        val price: Int?
    )

    data class StoreFilterDto(
        val id: Long,
        val name: String,
        val foodType: FoodType,
        val isZeropay: Boolean,
        val walkTime: Int,
        val isOverPerson: Boolean,
        val storeMenuList: List<StoreMenuDto>
    )


}