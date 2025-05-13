package io.sprout.api.user.controller

import io.sprout.api.common.model.entities.PageResponse
import io.sprout.api.mypage.dto.*
import io.sprout.api.user.model.dto.*
import io.sprout.api.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/users")
class AdminUserController(
    private val userService: UserService,
) {

    /**
     * 관리자의 사용자 목록 조회
     */
    @GetMapping()
    @Operation(summary = "사용자 목록", description = "자신의 관리범위 내 사용자 목록을 조회한다.")
    fun getUsers(searchRequest: UserSearchRequestDto): ResponseEntity<PageResponse<UserSearchResponseDto>> {

        val result = userService.searchUsers(searchRequest)

        return ResponseEntity.ok(result)
    }

    /**
     * 관리자의 사용자 역할 변경
     */
    @PatchMapping("/role/{userId}")
    @Operation(summary = "권한 변경", description = "사용자의 권한을 변경한다.")
    fun changeRole(
        @RequestBody updateRequest: RoleUpdateRequestDto,
        @PathVariable userId: Long
    ): ResponseEntity<Any> {
        userService.changeRoleByAdmin(updateRequest, userId)

        return ResponseEntity.ok().build()
    }

    /**
     * 관리자의 사용자 조회
     */
    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회", description = "V1. Mypage profile과 동일한 내용 전달")
    fun getUserDetail(@PathVariable userId: Long): ResponseEntity<UserDetailResponse> {
        val result = userService.getUserDetailByAdmin(userId)

        return ResponseEntity.ok(result)
    }

//    @Operation(summary = "작성 글 조회", description = "작성한 글들의 ID를 반환합니다.")
//    @GetMapping("/{userId}/post")
//    fun getPostList(@PathVariable userId: Long): ResponseEntity<List<PostAndNickNameDto>> {
//        return ResponseEntity.ok(userService.getPostListByUserId(userId))
//    }

    @Operation(summary = "작성 댓글 조회", description = "작성한 댓글들의 ID와 게시글 ID를 반환합니다.")
    @GetMapping("/{userId}/comments")
    fun getCommentList(@PathVariable userId: Long): ResponseEntity<List<PostCommentDto>> {
        return ResponseEntity.ok(userService.getPostCommentListByUserId(userId))
    }

    @Operation(summary = "찜한 글 조회", description = "찜한 글들의 ID를 반환합니다.")
    @GetMapping("/{userId}/scrap")
    fun getPostScrapList(
        @PathVariable userId: Long,
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<Page<GetPostResponseDto>> {
        return ResponseEntity.ok(userService.getPostScrapListByUserId(userId, pageable))
    }

    @Operation(summary = "신청한 글 조회 (제목만)", description = "신청한 글을 반환합니다.")
    @GetMapping("/{userId}/participantTitle")
    fun getPostParticipantIDList(@PathVariable userId: Long): ResponseEntity<ParticipantListResponseDto> {
        return ResponseEntity.ok(userService.getPostParticipantListByUserId(userId))
    }


    /**
     * 관리자의 사용자 전화번호 변경
     */
    @PatchMapping("{userId}")
    @Operation(summary = "사용자 번호 변경", description = "사용자의 번호를 변경한다.")
    fun changePhone(@PathVariable userId: Long, @RequestBody phoneNumber: PhoneNumber): ResponseEntity<String> {
        return ResponseEntity.ok(userService.changePhoneByUserId(userId, phoneNumber))
    }


    /**
     * 관리자의 사용자 역할 변경
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "사용자 탈퇴 -> V1. 개인 정보 삭제 후 익명회원으로 전환")
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<Any> {
        userService.deleteUser(userId)

        return ResponseEntity.ok().build()
    }


    /**
     * 관리자의 학생 목록 조회
     */
    @GetMapping("/trainees")
    @Operation(summary = "학생 목록", description = "자신의 관리범위 내 학생 목록을 조회한다.")
    fun getTrainees(searchRequest: UserSearchRequestDto): ResponseEntity<PageResponse<TraineeSearchResponseDto>> {

        val result = userService.searchTrainees(searchRequest)

        return ResponseEntity.ok(result)
    }

    /**
     * 학생에 대한 메모 작성 및 수정
     * 한 매니저가 한 학생에게 하나의 메모만 작성 가능하다.
     */
    @PostMapping("/trainees/{traineeId}/memo")
    @Operation(summary = "학생에 대한 메모 작성 및 수정", description = "자신의 관리범위 내 학생의 단순 메모를 작성한다. 메모는 하나뿐이다.")
    fun createMemo(@RequestBody createRequest: UserMemoCreateRequestDto, @PathVariable traineeId: Long): ResponseEntity<Any> {

        userService.createUserMemo(createRequest, traineeId)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/trainees/{traineeId}/memo")
    @Operation(summary = "학생에 대한 메모 조회", description = "학생의 메모를 조회한다. 값이 없다면 빈 body가 return 된다.")
    fun getMemo(@PathVariable traineeId: Long): ResponseEntity<UserMemoResponseDto> {

        val userMemo = userService.getUserMemo(traineeId)

        return ResponseEntity.ok(userMemo)
    }

    /**
     * 학생에 대한 메모 삭제
     */
    @DeleteMapping("/trainees/{traineeId}/memo")
    @Operation(summary = "학생에 대한 메모 삭제", description = "자신의 관리범위 내 학생의 단순 메모를 삭제한다.")
    fun deleteMemo(@PathVariable traineeId: Long): ResponseEntity<Any> {

        userService.deleteUserMemo(traineeId)

        return ResponseEntity.ok().build()
    }

}