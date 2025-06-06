package io.sprout.api.store.model.dto

import com.nimbusds.openid.connect.sdk.assurance.evidences.Voucher
import io.sprout.api.store.model.entities.FoodType

class StoreProjectionDto{

    //위도, 경도, 스크랩 여부, 스크랩 개수
    data class StoreInfoDto @JvmOverloads constructor(
        val id: Long,
        val name: String,
        val foodType: FoodType,
        val campusName: String,
        val mapSchemaId: String,
        val address: String,
        val contact: String,
        val workingDay: String,
        val breakTime: String,
        val holiday: String,
        val isZeropay: Boolean,
        val walkTime: Int,
        val isOverPerson: Boolean,
        val longitude: String,
        val latitude: String,
        val scrapCount: Long,
        val isScrap: Boolean,
        var postId: Long? = null,
        var isScraped: Boolean = false
    ) {
        var storeImageList: MutableList<StoreImageDto> = mutableListOf()
        var storeMenuList: MutableList<StoreMenuDto> = mutableListOf()
        val isLessThan10000Menu: Boolean
            get() {
                return storeMenuList.any { it.price!! <= 10000 }
            }
    }

    data class StoreImageDto(
        val id: Long?,
        val path: String?
    )

    data class StoreMenuDto(
        val id: Long?,
        val name: String?,
        val price: Int?,
        val imageUrl: String?
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

    data class FoodTypeCount(
        val foodType: FoodType,
        val count: Long
    )

    data class StoreOptionCount(
        val isZeropayCount: Long,
        val isVoucherCount: Long,
        val isOverPerson: Long
    ){
        var isLessThan10000Price: Long = 0L
    }

    data class StoreFilterCount(
        val foodTypeCount: List<FoodTypeCount>,
        val storeOptionCount: StoreOptionCount
    )




}