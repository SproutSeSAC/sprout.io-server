package io.sprout.api.scrap.service

import io.sprout.api.scrap.dto.ScrapRequestDto
import io.sprout.api.scrap.dto.ScrapResponseDto
import io.sprout.api.scrap.entity.ScrapEntity
import io.sprout.api.scrap.repository.ScrapRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ScrapService(
        private val scrapRepository: ScrapRepository
) {

    @Transactional
    fun addScrap(dto: ScrapRequestDto): ScrapResponseDto {
        val existingScrap = scrapRepository.findByUserIdAndPostId(dto.userId, dto.postId)
        if (existingScrap != null) {
            throw IllegalArgumentException("이미 스크랩한 게시글.")
        }

        val scrap = ScrapEntity(userId = dto.userId, postId = dto.postId)
        val savedScrap = scrapRepository.save(scrap)

        return convertToDto(savedScrap)
    }

    @Transactional(readOnly = true)
    fun getScrapsByUserId(userId: Long): List<ScrapResponseDto> {
        val scraps = scrapRepository.findByUserId(userId)
        return scraps.map { convertToDto(it) }
    }

    @Transactional
    fun deleteScrap(userId: Long, postId: Long): Boolean {
        val scrap = scrapRepository.findByUserIdAndPostId(userId, postId)
                ?: throw EntityNotFoundException("존재하지 않는 POST ID")

        scrapRepository.delete(scrap)
        return true
    }

    @Transactional
    fun deleteAllScrapsByUserId(userId: Long): Boolean {
        val scraps = scrapRepository.findByUserId(userId)
        if (scraps.isNotEmpty()) {
            scrapRepository.deleteAll(scraps)
        }
        return true
    }

    private fun convertToDto(scrap: ScrapEntity): ScrapResponseDto {
        return ScrapResponseDto(
                id = scrap.id,
                userId = scrap.userId,
                postId = scrap.postId,
                createdAt = LocalDateTime.now()
        )
    }
}
