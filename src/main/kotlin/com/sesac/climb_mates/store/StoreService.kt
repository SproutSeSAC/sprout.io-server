package com.sesac.climb_mates.store

import com.sesac.climb_mates.account.data.AccountRepository
import com.sesac.climb_mates.store.data.Menu
import com.sesac.climb_mates.store.data.MenuRepository
import com.sesac.climb_mates.store.data.Store
import com.sesac.climb_mates.store.data.StoreRepository
import com.sesac.climb_mates.store.data.img.StoreImage
import com.sesac.climb_mates.store.data.img.StoreImageRepository
import com.sesac.climb_mates.store.data.review.StoreReview
import com.sesac.climb_mates.store.data.review.StoreReviewDTO
import com.sesac.climb_mates.store.data.review.StoreReviewRepository
import com.sesac.climb_mates.store.data.time.StoreTime
import com.sesac.climb_mates.store.data.time.StoreTimeRepository
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class StoreService(
    private val storeRepository: StoreRepository,
    private val menuRepository: MenuRepository,
    private val storeTimeRepository: StoreTimeRepository,
    private val storeImageRepository: StoreImageRepository,
    private val storeReviewRepository: StoreReviewRepository,
    private val accountRepository: AccountRepository
) {
    fun getStoreByStyle(style: String): List<Store> {
        return when(style){
            "default" -> storeRepository.findAll()
            "제로페이" -> storeRepository.findByIsZero(1)
            "식대" -> storeRepository.findByIsSupport(1)
            else -> storeRepository.findByStyle(style)
        }
    }

    fun getStoreById(id:Long): Store {
        return storeRepository.findById(id).get()
    }

    fun getStoreByName(storeName:String): Store {
        return storeRepository.findByName(storeName).getOrElse {
            Store(
                id=-1,
                name="",
                location = "",
                attr = "",
                lat="", lon = "",
                style = "",
            )
        }
    }

    fun getMenuByStoreId(storeId: Long): List<Menu> {
        return menuRepository.findByStoreId(storeId)
    }

    fun getStoreTimeByStoreId(storeId:Long): Optional<StoreTime> {
        return storeTimeRepository.findByStoreId(storeId)
    }

    fun getStoreImageByStoreId(storeId: Long): List<StoreImage> {
        return storeImageRepository.findByStoreId(storeId)
    }

    fun getStoreReviewByStoreId(storeId: Long): List<StoreReview> {
        return storeReviewRepository.findByStoreId(storeId)
    }

    fun createStore(store: Store): Store {
        return storeRepository.save(store)
    }

    fun createMenu(menu: Menu): Menu {
        return menuRepository.save(menu)
    }

    fun createStoreTime(storeTime: StoreTime): StoreTime {
        return storeTimeRepository.save(storeTime)
    }

    fun createStoreImage(storeImage: StoreImage): StoreImage {
        return storeImageRepository.save(storeImage)
    }

    fun createReview(review: StoreReviewDTO, user: User): StoreReview {
        return try{
            storeReviewRepository.save(
                StoreReview(
                    store = storeRepository.findById(review.storeId).get(),
                    account = accountRepository.findByUsername(user.username).get(),
                    content = review.content,
                    star = review.star
                )
            )
        }catch (e:Exception){
            StoreReview(
                id = -1L,
                store = storeRepository.findById(review.storeId).get(),
                account = accountRepository.findByUsername(user.username).get(),
                content = "-1",
                star = 0
            )
        }
    }

    fun deleteReview(reviewId: Long) {
        storeReviewRepository.deleteById(reviewId)
    }
}