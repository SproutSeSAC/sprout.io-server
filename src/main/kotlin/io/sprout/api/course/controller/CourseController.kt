package io.sprout.api.course.controller

import io.sprout.api.course.model.dto.CourseDto
import io.sprout.api.course.service.CourseService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
}