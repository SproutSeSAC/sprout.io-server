package io.sprout.api.project.service

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.common.exeption.custom.CustomDataIntegrityViolationException
import io.sprout.api.common.exeption.custom.CustomSystemException
import io.sprout.api.common.exeption.custom.CustomUnexpectedException
import io.sprout.api.position.model.entities.PositionEntity
import io.sprout.api.project.model.dto.*
import io.sprout.api.project.model.entities.ProjectCommentEntity
import io.sprout.api.project.model.entities.ProjectPositionEntity
import io.sprout.api.project.model.entities.ProjectTechStackEntity
import io.sprout.api.project.model.entities.ScrapedProjectEntity
import io.sprout.api.project.repository.*
import io.sprout.api.specification.model.entities.TechStackEntity
import io.sprout.api.user.model.entities.UserEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service
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

    override fun postProject(projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean {
        return handleExceptions {
            val projectEntity = projectRecruitmentRequestDTO.toEntity(securityManager.getAuthenticatedUserName())
            val savedProjectEntity = projectRepository.save(projectEntity)
            projectRecruitmentRequestDTO.positions.forEach {
                val selectedPosition = PositionEntity(it)
                val projectPositionEntity = ProjectPositionEntity(savedProjectEntity, selectedPosition)
                projectPositionRepository.save(projectPositionEntity)
            }
            projectRecruitmentRequestDTO.requiredStacks.forEach {
                val requiredTechStack = TechStackEntity(it, "", true)
                val techStack = ProjectTechStackEntity(savedProjectEntity, requiredTechStack)
                projectTechStackRepository.save(techStack)
            }
            true
        }
    }

    override fun getFilteredProjects(filterRequest: ProjectFilterRequest): Pair<List<ProjectResponseDto>, Long> {
        return handleExceptions {
            val validPage = if (filterRequest.page < 1) 1 else filterRequest.page
            val validSize = if (filterRequest.size <= 0) 20 else filterRequest.size
            val updatedFilterRequest = filterRequest.copy(page = validPage, size = validSize)
            projectRepository.filterProjects(updatedFilterRequest, securityManager.getAuthenticatedUserName()!!)
        }
    }

    override fun toggleScrapProject(projectId: Long): Boolean {
        return handleExceptions {
            val user = UserEntity(securityManager.getAuthenticatedUserName()!!)
            val project = projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Project not found") }
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

    override fun increaseViewCount(projectId: Long): Boolean {
        return handleExceptions {
            val project = projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Project not found") }
            project.viewCount += 1
            projectRepository.save(project)
            true
        }
    }

    override fun findProjectDetailById(projectId: Long): ProjectDetailResponseDto? {
        return handleExceptions {
            projectRepository.findProjectDetailById(projectId) ?: throw IllegalArgumentException("Project with ID $projectId not found")
        }
    }

    override fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto> {
        return handleExceptions {
            projectRepository.getCommentsByProjectId(projectId)
        }
    }

    override fun postComment(projectId: Long, content: String): Boolean {
        return handleExceptions {
            val writer = UserEntity(securityManager.getAuthenticatedUserName()!!)
            val project = projectRepository.findById(projectId).orElseThrow { IllegalArgumentException("Invalid project ID: $projectId") }
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

    override fun updateProject(projectId: Long, projectRecruitmentRequestDTO: ProjectRecruitmentRequestDto): Boolean {
        return handleExceptions {
            val projectEntity = projectRepository.findById(projectId)
                .orElseThrow { IllegalArgumentException("Project not found") }

            projectEntity.updateFromDto(projectRecruitmentRequestDTO)

            projectRepository.save(projectEntity)

            true
        }
    }

    override fun getProjectsEndingTomorrow(): List<ProjectSimpleResponseDto> {
        val yesterday = LocalDate.now().plusDays(1)
        return projectRepository.findProjectsEndingTommorowWithDetails(yesterday)
    }
}
