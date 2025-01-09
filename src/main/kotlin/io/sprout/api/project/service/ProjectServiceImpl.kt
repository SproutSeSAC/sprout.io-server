package io.sprout.api.project.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.position.model.entities.PositionEntity
import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.*
import io.sprout.api.project.repository.*
import io.sprout.api.specification.model.entities.jobEntityOf
import io.sprout.api.specification.model.entities.TechStackEntity
import io.sprout.api.user.model.entities.UserEntity
import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val securityManager: SecurityManager,
    private val projectPositionRepository: ProjectPositionRepository,
    private val projectTechStackRepository: ProjectTechStackRepository,
    private val scrapedProjectRepository: ScrapedProjectRepository,
    private val projectCommentRepository: ProjectCommentRepository
) : ProjectService {

    private fun <T> handleExceptions(action: () -> T): T {
        return try {
            action()
        } catch (e: DataIntegrityViolationException) {
            throw CustomDataIntegrityViolationException("Data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            throw CustomSystemException("System error occurred: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw CustomUnexpectedException("Invalid input: ${e.message}")
        } catch (e: Exception) {
            throw CustomUnexpectedException("Unexpected error occurred: ${e.message}")
        }
    }

    @Transactional
    override fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean {
        return handleExceptions {
            val projectEntity = projectRecruitmentRequestDTO.toEntity(securityManager.getAuthenticatedUserName())
            val savedProjectEntity = projectRepository.save(projectEntity)
            saveProjectPositions(savedProjectEntity, projectRecruitmentRequestDTO.positions)
            saveProjectTechStacks(savedProjectEntity, projectRecruitmentRequestDTO.requiredStacks)
            true
        }
    }

    @Transactional
    override fun getFilteredProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long> {
        return handleExceptions {
            val validPage = if (filterRequest.page < 1) 1 else filterRequest.page
            val validSize = if (filterRequest.size <= 0) 20 else filterRequest.size
            val updatedFilterRequest = filterRequest.copy(page = validPage, size = validSize)
            projectRepository.filterProjects(updatedFilterRequest, securityManager.getAuthenticatedUserName()!!)
        }
    }

    @Transactional
    override fun toggleScrapProject(projectId: Long): Boolean {
        return handleExceptions {
            val user = UserEntity(securityManager.getAuthenticatedUserName()!!)
            val project =
                projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Project not found") }
            val existingScrap = scrapedProjectRepository.findByUserAndProject(user, project)
            if (existingScrap != null) {
                scrapedProjectRepository.delete(existingScrap)
                false
            } else {
                val newScrap = ScrapedProjectEntity(user = user, project = project)
                scrapedProjectRepository.save(newScrap)
                true
            }
        }
    }

    @Transactional
    override fun increaseViewCount(projectId: Long): Boolean {
        return handleExceptions {
            val project =
                projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Project not found") }
            project.viewCount += 1
            projectRepository.save(project)
            true
        }
    }

    @Transactional
    override fun findProjectDetailById(projectId: Long): ProjectDetailResponseDto? {
        return handleExceptions {
            projectRepository.findProjectDetailById(projectId, securityManager.getAuthenticatedUserName()!!)
                ?: throw IllegalArgumentException("Project with ID $projectId not found")
        }
    }

    @Transactional(readOnly = true)
    override fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto> {
        return handleExceptions {
            projectRepository.getCommentsByProjectId(projectId)
        }
    }

    @Transactional
    override fun postComment(projectId: Long, content: String): Boolean {
        return handleExceptions {
            val writer = UserEntity(securityManager.getAuthenticatedUserName()!!)
            val project = projectRepository.findById(projectId)
                .orElseThrow { IllegalArgumentException("Invalid project ID: $projectId") }
            val comment = ProjectCommentEntity(content = content, writer = writer, project = project)
            projectCommentRepository.save(comment)
            true
        }
    }

    override fun deleteComment(commentId: Long): Boolean {
        return handleExceptions {
            projectCommentRepository.deleteById(commentId)
            true
        }
    }

    override fun deleteProject(projectId: Long): Boolean {
        return handleExceptions {
            projectRepository.deleteById(projectId)
            true
        }
    }

    @Transactional
    override fun updateProject(projectId: Long, projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean {
        return handleExceptions {
            val projectEntity = projectRepository.findById(projectId)
                .orElseThrow { IllegalArgumentException("Project not found") }
            projectEntity.updateFromDto(projectRecruitmentRequestDTO)

            projectEntity.positions.forEach {
                projectPositionRepository.delete(it)
            }
            projectEntity.positions = mutableSetOf()

            projectEntity.techStacks.forEach {
                projectTechStackRepository.deleteById(it.id)
            }
            projectEntity.techStacks = mutableSetOf()


            saveProjectPositions(projectEntity, projectRecruitmentRequestDTO.positions)
            saveProjectTechStacks(projectEntity, projectRecruitmentRequestDTO.requiredStacks)
            true
        }
    }

    override fun getProjectsEndingTomorrow(): List<ProjectSimpleResponseDto> {
        val yesterday = LocalDate.now().plusDays(1)
        return projectRepository.findProjectsEndingTommorowWithDetails(yesterday)
    }

    private fun saveProjectPositions(savedProjectEntity: ProjectEntity, positions: List<Long>) {
        positions.forEach {
            val projectPositionEntity = ProjectPositionEntity(savedProjectEntity, jobEntityOf(it))
            projectPositionRepository.save(projectPositionEntity)
        }
    }

    private fun saveProjectTechStacks(savedProjectEntity: ProjectEntity, stacks: List<Long>) {
        stacks.forEach {
            val requiredTechStack = TechStackEntity(it, "", true)
            val techStack = ProjectTechStackEntity(savedProjectEntity, requiredTechStack)
            projectTechStackRepository.save(techStack)
        }
    }

    @Transactional
    override fun postProjectAndGetId(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Long {
        val projectEntity = projectRecruitmentRequestDTO.toEntity(securityManager.getAuthenticatedUserName())
        val savedProjectEntity = projectRepository.save(projectEntity)
        saveProjectPositions(savedProjectEntity, projectRecruitmentRequestDTO.positions)
        saveProjectTechStacks(savedProjectEntity, projectRecruitmentRequestDTO.requiredStacks)
        return savedProjectEntity.id
    }

    override fun getProjectTitleById(linkedId: Long): String {
        val project = projectRepository.findById(linkedId)
                .orElseThrow { EntityNotFoundException("프로젝트를 찾을 수 없습니다. ID: $linkedId") }
        return project.title ?: "No Title"
    }
}
