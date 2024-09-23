package io.sprout.api.campus.controller

import io.sprout.api.campus.model.dto.CampusDto
import io.sprout.api.campus.service.CampusService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/campus")
class CampusController(
    private val campusService: CampusService
) {

    @GetMapping("/list")
    @Operation(summary = "모든 캠퍼스 리스트 조회", description = "모든 캠퍼스 리스트 조회")
    fun getCampusList(): CampusDto.CampusListResponse {
        return campusService.getCampusList()
    }
}