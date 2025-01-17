package io.sprout.api.course.controller

import io.sprout.api.course.model.dto.CourseRequestDto
import io.sprout.api.course.model.dto.CourseDto
import io.sprout.api.course.model.dto.CourseSearchRequestDto
import io.sprout.api.course.model.dto.CourseSearchResponseDto
import io.sprout.api.course.service.CourseService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/course")
class CourseController(
    private val courseService: CourseService
) {

    @GetMapping("/list/{campusId}")
    @Operation(summary = "코스 리스트 조회", description = "캠퍼스 내 개설된 코스 리스트 조회")
    fun getCourseListByCampusId(@PathVariable("campusId") campusId: Long): CourseDto.CourseListResponse {
        return courseService.getCourseListByCampusId(campusId)
    }

    /**
     *  관리자용
     *  교육과정 검색
     */
    @GetMapping
    @Operation(summary = "교육과정 검색", description = "관리자 전용, 자신이 속한 교육과정만 탐색 가능")
    fun courseSearch(@ModelAttribute searchRequest: CourseSearchRequestDto): ResponseEntity<CourseSearchResponseDto> {
        val searchCourses = courseService.searchCourse(searchRequest)

        return ResponseEntity.ok(searchCourses)
    }

    /**
     *  관리자용
     *  교육과정 추가
     */
    @PostMapping
    @Operation(summary = "교육과정 추가", description = "관리자 전용 교육과정 추가")
    fun createCourse(@RequestBody createRequest: CourseRequestDto): ResponseEntity<Any> {
        courseService.createCourse(createRequest)

        return ResponseEntity.ok().build()
    }

    /**
     *  관리자용
     *  교육과정 수정
     */
    @PutMapping("/{courseId}")
    @Operation(summary = "교육과정 수정", description = "관리자 전용 교육과정 수정")
    fun updateCourse(@RequestBody updateRequest: CourseRequestDto, @PathVariable courseId: Long): ResponseEntity<Any> {
        courseService.updateCourse(updateRequest, courseId)

        return ResponseEntity.ok().build()
    }

}