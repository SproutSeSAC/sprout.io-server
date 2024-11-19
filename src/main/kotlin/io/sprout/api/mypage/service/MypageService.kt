package io.sprout.api.mypage.service

import io.sprout.api.mypage.dto.CardDto
import io.sprout.api.mypage.dto.PostDto
import io.sprout.api.mypage.dto.PostParticipantDto
import io.sprout.api.mypage.dto.PostScrapDto
import io.sprout.api.mypage.entity.DummyPost
import io.sprout.api.mypage.entity.DummyPostParticipant
import io.sprout.api.mypage.entity.DummyPostScrap
import io.sprout.api.mypage.repository.*
import io.sprout.api.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class MypageService(
        private val userRepository: UserRepository,
        private val userCampusRepository: UserCampusRepository,
        private val userCourseRepository: UserCourseRepository,
        private val dummyPostScrapRepository: DummyPostScrapRepository,
        private val dummyPostRepository: DummyPostRepository,
        private val dummyPostParticipantRepository: DummyPostParticipantRepository
) {

    // region [프로필 관련 API]

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

    // endregion

    // region [게시글 관련 API]

    // 작성 글 목록 조회
    fun getPostListByUserId(userId: Int): List<PostDto> {
        val posts: List<DummyPost> = dummyPostRepository.findAllByUserId(userId)

        // DTO 변환
        return posts.map {
            PostDto(
                    postId = it.postid,
                    userId = it.userId
            )
        }
    }

    // 찜한 글 목록 조회
    fun getPostScrapListByUserId(userId: Int): List<PostScrapDto> {
        val scraps: List<DummyPostScrap> = dummyPostScrapRepository.findAllByUserId(userId)

        // DTO 변환
        return scraps.map {
            PostScrapDto(
                    postScrapId = it.postscrapid,
                    userId = it.userId
            )
        }
    }

    // 참여 글 목록 조회
    fun getPostParticipantListByUserId(userId: Int): List<PostParticipantDto> {
        val particis: List<DummyPostParticipant> = dummyPostParticipantRepository.findAllByUserId(userId)

        // DTO 변환
        return particis.map {
            PostParticipantDto(
                    postParticipantId = it.postparticipantid,
                    userId = it.userId
            )
        }
    }
    // endregion
}