package io.sprout.api.mealPost.service

import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomBadRequestException
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.mealPost.model.dto.MealPostDto
import io.sprout.api.mealPost.model.dto.MealPostProjection
import io.sprout.api.mealPost.model.entities.MealPostEntity
import io.sprout.api.mealPost.model.entities.MealPostParticipationEntity
import io.sprout.api.mealPost.model.entities.MealPostStatus
import io.sprout.api.mealPost.repository.MealPostParticipationRepository
import io.sprout.api.mealPost.repository.MealPostRepository
import io.sprout.api.store.repository.StoreRepository
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.*
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service

@Service
class MealPostService(
    private val mealPostRepository: MealPostRepository,
    private val mealPostParticipationRepository: MealPostParticipationRepository,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val securityManager: SecurityManager
) {

    private val log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler::class.java)

    fun getMealPostList(pageable: Pageable): List<MealPostProjection> {
        return mealPostRepository.findMealPostList(pageable, getUserInfo().id)
    }

    fun createMealPost(request: MealPostDto.MealPostCreateRequest) {

        val user = getUserInfo()
        val mealPostEntity = MealPostEntity(
            title = request.title?: "",
            appointmentTime = request.appointmentTime,
            memberCount = request.memberCount,
            meetingPlace = request.meetingPlace,
            mealPostStatus = MealPostStatus.ACTIVE,
            storeName = request.storeName
        )

        mealPostEntity.mealPostParticipationList.plusAssign(
            MealPostParticipationEntity(
                user = user,
                mealPost = mealPostEntity,
                ordinalNumber = 1
            )
        )

        try {
            val mealPost = mealPostRepository.save(mealPostEntity)
            log.debug("createMealPost, mealPostId is: {}", mealPost.id )

        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("User data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while saving meal post: ${e.message}")
        }
    }

    fun deleteMealPost(mealPostId: Long) {
        val mealPost = mealPostRepository.findById(mealPostId).orElseThrow { CustomBadRequestException("Not found party") }
        if (! mealPostParticipationRepository.isOwner(mealPostId, getUserInfo().id)) {
            throw CustomBadRequestException("not party owner")
        }

        mealPost.mealPostParticipationList.clear()

        try {
            mealPostRepository.delete(mealPost)
            log.debug("deleteMealPost, mealPostId was: {}", mealPostId)
        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("User data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while saving meal post: ${e.message}")
        }

    }

    fun joinParty(request: MealPostDto.ParticipationRequest) {

        val mealPost = mealPostRepository.findById(request.mealPostId).orElseThrow { CustomBadRequestException("Not found party") }
        val user = getUserInfo()

        if (mealPost.mealPostStatus != MealPostStatus.ACTIVE) {
            throw CustomBadRequestException("Party is not active")
        }

        if (mealPost.mealPostParticipationList.map { it.user.id }.contains(user.id)) {
            throw CustomBadRequestException("Already joined user")
        }

        if (mealPost.countJoinMember() >= mealPost.memberCount) {
            throw CustomBadRequestException("Party is already full")
        }

        mealPost.mealPostParticipationList.plusAssign(
            MealPostParticipationEntity(
                mealPost = mealPost,
                user = user,
                ordinalNumber = 2
            )
        )

        try {
            mealPostRepository.save(mealPost)
            log.debug("JoinParty, mealPostId is: ${mealPost.id}, userID is ${user.id}")
        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("User data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while saving meal post participation: ${e.message}")
        }

    }

    fun leaveParty(request: MealPostDto.LeaveRequest) {

        val user = getUserInfo()
        val mealPost = mealPostRepository.findById(request.mealPostId).orElseThrow { CustomBadRequestException("Not found party") }
        val mealPostParticipation = mealPost.mealPostParticipationList.firstOrNull { it.user.id == user.id }

        if (mealPostParticipation?.ordinalNumber == 1) {
            throw CustomBadRequestException("post owner's leave is not allowed")
        }

        mealPost.mealPostParticipationList.remove(mealPostParticipation)

        try {
            mealPostRepository.save(mealPost)
            log.debug("LeaveParty, mealPostId is: ${mealPost.id}, userID is ${user.id}")
        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("User data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while leaving party: ${e.message}")
        }

    }

    private fun getUserInfo(): UserEntity {
        val userId = securityManager.getAuthenticatedUserName() ?: throw CustomBadRequestException("Check access token")
        return userRepository.findById(userId).orElseThrow { CustomBadRequestException("Not found user") }
    }

    fun getMealPostDetail(mealPostId: Long): MealPostDto.MealPostDetailResponse {
        val mealPost: MealPostEntity = mealPostRepository.findWithParticipationUserById(mealPostId)
            ?: throw CustomBadRequestException("Not found party")

        return MealPostDto.MealPostDetailResponse(mealPost)
    }
}