package io.sprout.api.scrap.controller

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.scrap.dto.ScrapRequestDto
import io.sprout.api.scrap.dto.ScrapResponseDto
import io.sprout.api.scrap.service.ScrapService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/scraps")
class ScrapController(
        private val scrapService: ScrapService,
        private val securityManager: SecurityManager
) {

    @PostMapping("/{postId}")
    @Operation(summary = "스크랩 추가", description = "게시글을 스크랩합니다.")
    fun addScrap(@PathVariable postId: Long): ResponseEntity<ScrapResponseDto> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val scrap = scrapService.addScrap(ScrapRequestDto(userId = clientID, postId = postId))
        return ResponseEntity.ok(scrap)
    }

    @GetMapping
    @Operation(summary = "사용자 스크랩 조회", description = "사용자가 스크랩한 게시글을 조회합니다.")
    fun getScrapsByUserId(): ResponseEntity<List<ScrapResponseDto>> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val scraps = scrapService.getScrapsByUserId(clientID)
        return ResponseEntity.ok(scraps)
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "스크랩 삭제", description = "사용자의 특정 게시글 스크랩을 삭제합니다.")
    fun deleteScrap(@PathVariable postId: Long): ResponseEntity<Boolean> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val result = scrapService.deleteScrap(clientID, postId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping
    @Operation(summary = "사용자 스크랩 전체 삭제", description = "사용자의 모든 스크랩을 삭제합니다.")
    fun deleteAllScrapsByUserId(): ResponseEntity<Boolean> {
        val clientID = securityManager.getAuthenticatedUserName()
                ?: return ResponseEntity.status(401).build()

        val result = scrapService.deleteAllScrapsByUserId(clientID)
        return ResponseEntity.ok(result)
    }
}
