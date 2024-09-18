package io.sprout.api.project.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.position.model.entities.PositionEntity
import io.sprout.api.project.model.dto.ProjectRecruitmentRequestDTO
import io.sprout.api.project.model.entities.ProjectPositionEntity
import io.sprout.api.project.model.entities.ProjectTechStackEntity
import io.sprout.api.project.repository.ProjectPositionRepository
import io.sprout.api.project.repository.ProjectRepository
import io.sprout.api.project.repository.ProjectTechStackRepository
import io.sprout.api.specification.model.entities.TechStackEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val securityManager: SecurityManager,
    private val projectPositionRepository: ProjectPositionRepository,
    private val projectTechStackRepository: ProjectTechStackRepository
) : ProjectService {
    override fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDTO): Boolean {
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
                val requiredTechStack = TechStackEntity(it, true)
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
}