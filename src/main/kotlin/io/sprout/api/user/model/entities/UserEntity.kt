package io.sprout.api.user.model.entities

import io.sprout.api.campus.model.entities.CampusEntity
import io.sprout.api.common.model.entities.BaseEntity
import io.sprout.api.course.model.entities.CourseEntity
import io.sprout.api.specification.model.entities.DomainEntity
import io.sprout.api.specification.model.entities.JobEntity
import io.sprout.api.specification.model.entities.TechStackEntity
import io.sprout.api.user.model.dto.CreateUserRequest
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
class UserEntity(

    @Column(nullable = false, length = 50, unique = true)
    var nickname: String, // 닉네임

    @Column(unique = true, nullable = false, length = 50)
    var email: String, // 이메일

    @Column(name = "profile_image_url", nullable = true, length = 100)
    var profileImageUrl: String?, // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var role: RoleType, // 유저 권한(= 유형)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var status: UserStatus, // 유저 상태

    @Column(name = "is_essential")
    var isEssential: Boolean


) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 20)
    var name: String? = null // 유저명

    @Column(length = 20)
    var phoneNumber: String = "" // 전화번호

    @Column(name = "refresh_token", length = 700)
    var refreshToken: String? = null // 리프레시 토큰

    @Column(name = "last_login_date_time")
    var lastLoginDateTime: LocalDateTime? = null // 마지막 로그인 시간

    var serviceTerms: Boolean = true

    var personalInformationTerms = true

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var userJobList: MutableSet<UserJobEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL],orphanRemoval = true, fetch = FetchType.LAZY)
    var userDomainList: MutableSet<UserDomainEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true,fetch = FetchType.LAZY)
    var userTechStackList: MutableSet<UserTechStackEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true,fetch = FetchType.LAZY)
    var userCourseList: MutableSet<UserCourseEntity> = LinkedHashSet()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true,fetch = FetchType.LAZY)
    var userCampusList: MutableSet<UserCampusEntity> = LinkedHashSet()

    fun addRefreshToken(refreshToken: String){
        this.refreshToken = refreshToken
    }

    constructor(id: Long) : this(
        nickname = "",
        email = "",
        profileImageUrl = null,
        role = RoleType.TRAINEE, // 기본 값 설정
        status = UserStatus.ACTIVE, // 기본 값 설정
        isEssential = false,
    ) {
        this.id = id
    }

    fun register(
        request: CreateUserRequest,
        courseList: MutableList<CourseEntity>,
        campusList: MutableList<CampusEntity>,
        jobList: MutableList<JobEntity>,
        domainList: MutableList<DomainEntity>,
        techStackList: MutableList<TechStackEntity>){

        userCourseList.clear()
        userCampusList.clear()
        userJobList.clear()
        userDomainList.clear()
        userTechStackList.clear()

        name = request.name
        nickname = request.nickname
        phoneNumber = request.phoneNumber
        role = request.role
        status = UserStatus.ACTIVE
        serviceTerms = request.serviceTerms
        personalInformationTerms = request.personalInformationTerms
        isEssential = true


        courseList.map {
            UserCourseEntity(it, this)
        }.forEach {userCourseList.add(it)}
        campusList.map {
            UserCampusEntity(it, this)
        }.forEach {userCampusList.add(it)}
        jobList.map {
            UserJobEntity(it, this)
        }.forEach {userJobList.add(it)}
         domainList.map {
            UserDomainEntity(it, this)
        }.forEach {userDomainList.add(it)}
        techStackList.map {
            UserTechStackEntity(it, this)
        } .forEach {userTechStackList.add(it)}
    }

}

enum class UserStatus {
    ACTIVE, INACTIVE, SLEEP, LEAVE
}

enum class RoleType {
    SUPER_ADMIN, CAMPUS_LEADER, OPERATION_MANAGER, EDU_MANAGER, INSTRUCTOR, JOB_COORDINATOR, TRAINEE, PRE_TRAINEE,
}