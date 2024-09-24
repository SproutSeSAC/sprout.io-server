package io.sprout.api.project.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.position.model.entities.PositionEntity
import io.sprout.api.project.model.dto.ProjectFilterRequest
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDto
import io.sprout.api.project.model.dto.ProjectResponseDto
import io.sprout.api.project.model.entities.ProjectPositionEntity
import io.sprout.api.project.model.entities.ProjectTechStackEntity
import io.sprout.api.project.model.entities.ScrapedProjectEntity
import io.sprout.api.project.repository.ProjectPositionRepository
import io.sprout.api.project.repository.ProjectRepository
import io.sprout.api.project.repository.ProjectTechStackRepository
import io.sprout.api.project.repository.ScrapedProjectRepository
import io.sprout.api.techStack.model.entities.TechStackEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val securityManager: SecurityManager,
    private val projectPositionRepository: ProjectPositionRepository,
    private val projectTechStackRepository: ProjectTechStackRepository,
    private val scrapedProjectRepository: ScrapedProjectRepository,
) : ProjectService {
    override fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean {
        try {
            val projectEntity = projectRecruitmentRequestDTO.toEntity(securityManager.getAuthenticatedUserName())

            // 엔티티 저장 및 저장된 엔티티 반환
            val savedProjectEntity = projectRepository.save(projectEntity)
            val projectId = savedProjectEntity.id
            //it 에 id 값들이 들어 있음
            projectRecruitmentRequestDTO.positions.map {
                val selectedPosition = PositionEntity(it)
                val projectPositionEntity = ProjectPositionEntity(savedProjectEntity, selectedPosition)
                projectPositionRepository.save(projectPositionEntity)
            }
            projectRecruitmentRequestDTO.requiredStacks.map {
                val requiredTechStack = TechStackEntity(it, "")
                val techStack = ProjectTechStackEntity(savedProjectEntity, requiredTechStack)
                projectTechStackRepository.save(techStack)
            }


            return true
        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 처리
            throw CustomDataIntegrityViolationException("Project data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 처리
            throw CustomSystemException("System error occurred while saving the project: ${e.message}")
        } catch (e: Exception) {
            // 예상치 못한 오류 처리
            throw CustomUnexpectedException("An unexpected error occurred: ${e.message}")
        }
    }

    override fun getFilteredProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long> {
        return projectRepository.filterProjects(filterRequest, securityManager.getAuthenticatedUserName()!!)
    }

    override fun toggleScrapProject(projectId: Long): Boolean {
        val user = UserEntity(securityManager.getAuthenticatedUserName()!!)
        val project =
            projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Project not found") }

        // 이미 스크랩했는지 확인
        val existingScrap = scrapedProjectRepository.findByUserAndProject(user, project)

        return if (existingScrap != null) {
            // 이미 스크랩한 경우: 스크랩 취소 (삭제)
            scrapedProjectRepository.delete(existingScrap)
            false
        } else {
            // 스크랩하지 않은 경우: 스크랩 추가
            val newScrap = ScrapedProjectEntity(
                user = user,
                project = project,
            )
            scrapedProjectRepository.save(newScrap)
            true
        }
    }


    override fun increaseViewCount(projectId: Long): Boolean {
        val project = projectRepository.findById(projectId)
            .orElseThrow { IllegalArgumentException("Project not found") }

        // viewCount 증가
        project.viewCount += 1

        // 변경된 값 저장
        projectRepository.save(project)
        return true
    }
}