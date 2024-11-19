package io.sprout.api.mypage.service

import io.sprout.api.mypage.dto.CardDto
import io.sprout.api.mypage.repository.UserCampusRepository
import io.sprout.api.mypage.repository.UserCourseRepository
import io.sprout.api.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class MypageService(
        private val userRepository: UserRepository,
        private val userCampusRepository: UserCampusRepository,
        private val userCourseRepository: UserCourseRepository
) {
    fun getUserCard(userId: Long): CardDto.UserCard {
        // UserEntity 조회
        val user = userRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("User not found") }

        // UserCampusEntity 조회
        val userCampus = userCampusRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("Campus not found") }

        // UserCourseEntity 조회
        val userCourse = userCourseRepository.findById(userId)
                .orElseThrow { IllegalArgumentException("Course not found") }

        // ProfileCard 생성
        val profileCard = CardDto.ProfileCard(
                name = user.name,
                nickname = user.nickname,
                profileUrl = user.profileImageUrl
        )

        // StudyCard 생성
        val studyCard = CardDto.StudyCard(
                email = user.email,
                campus = userCampus.campus,
                course = userCourse.course
        )

        // UserCard 생성
        return CardDto.UserCard(
                name = profileCard,
                nickname = studyCard
        )
    }
}