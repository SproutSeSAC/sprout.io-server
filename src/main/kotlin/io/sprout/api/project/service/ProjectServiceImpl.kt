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
import io.sprout.api.user.service.GoogleUserService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val securityManager: SecurityManager,
    private val projectPositionRepository: ProjectPositionRepository,
    private val projectTechStackRepository: ProjectTechStackRepository,
    private val scrapedProjectRepository: ScrapedProjectRepository,
    private val projectCommentRepository: ProjectCommentRepository
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
                val requiredTechStack = TechStackEntity(it, "", true)
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
        println("page:" + filterRequest.page)
        val validPage = if (filterRequest.page < 1) 1 else filterRequest.page
        val validSize = if (filterRequest.size <= 0) 20 else filterRequest.size
        val updatedFilterRequest = filterRequest.copy(page = validPage, size = validSize)
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

    override fun findProjectDetailById(projectId: Long): ProjectDetailResponseDto? {
        return try {
            val projectDetail = projectRepository.findProjectDetailById(projectId)
            projectDetail ?: throw IllegalArgumentException("Project with ID $projectId not found")
        } catch (e: IllegalArgumentException) {
            throw CustomUnexpectedException("Error occurred while fetching project details: ${e.message}")
        } catch (e: JpaSystemException) {
            throw CustomSystemException("Database system error occurred while fetching project details: ${e.message}")
        } catch (e: Exception) {
            throw CustomUnexpectedException("An unexpected error occurred: ${e.message}")
        }
    }

    override fun getCommentsByProjectId(projectId: Long): List<ProjectCommentResponseDto> {
        // 댓글을 생성일자(createdAt) 기준으로 오름차순으로 정렬하여 가져옴
        return projectRepository.getCommentsByProjectId(projectId)
    }

    override fun postComment(projectId: Long, content: String): Boolean {
        return try {
            // 인증된 사용자의 정보를 가져옴
            val writer = UserEntity(securityManager.getAuthenticatedUserName()!!)

            // 프로젝트 존재 여부 확인 및 예외 처리
            val project = projectRepository.findById(projectId)
                .orElseThrow { IllegalArgumentException("Invalid project ID: $projectId") }

            // 댓글 엔티티 생성
            val comment = ProjectCommentEntity(
                content = content,
                writer = writer,
                project = project
            )

            // 댓글 저장
            projectCommentRepository.save(comment)
            true // 성공 시 true 반환
        } catch (e: IllegalArgumentException) {
            // 프로젝트 ID가 잘못되었을 때
            throw CustomDataIntegrityViolationException("Invalid project ID: ${e.message}")
        } catch (e: DataIntegrityViolationException) {
            // 데이터 무결성 예외 (예: null 값 또는 데이터베이스 무결성 제약 조건 위반)
            throw CustomDataIntegrityViolationException("Data integrity violation: ${e.message}")
        } catch (e: JpaSystemException) {
            // 시스템 오류 (예: 데이터베이스 시스템 관련 문제)
            throw CustomSystemException("System error while saving comment: ${e.message}")
        } catch (e: Exception) {
            // 예상치 못한 오류
            throw CustomUnexpectedException("Unexpected error occurred: ${e.message}")
        }
    }
}