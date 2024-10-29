package io.sprout.api.user.service

import io.sprout.api.user.model.entities.GoogleCalendarEntity
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.config.properties.GoogleOAuthPropertiesConfig
import io.sprout.api.user.model.dto.CalendarIdResponseDto
import io.sprout.api.user.model.dto.ManagerEmailResponseDto
import io.sprout.api.user.model.entities.GoogleTokenEntity
import io.sprout.api.user.model.entities.RoleType
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.GoogleCalendarRepository
import io.sprout.api.user.repository.GoogleTokenRepository
import io.sprout.api.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.Instant

@Service
class GoogleTokenService(
    private val tokenRepository: GoogleTokenRepository,
    private val restTemplate: RestTemplate,
    private val googleOAuthProperties: GoogleOAuthPropertiesConfig,
    private val googleCalendarRepository: GoogleCalendarRepository,
    private val securityManager: SecurityManager,
    private val userRepository: UserRepository
) : GoogleUserService {
    override fun saveOrUpdateToken(user: UserEntity, accessToken: String, refreshToken: String?, expiresIn: Int) {
        val existingToken = tokenRepository.findByUser(user)
        if (existingToken != null) {
            // 기존 토큰이 있으면 Access Token과 만료 시간 갱신
            existingToken.updateAccessTokenAndRefreshToken(accessToken, refreshToken, expiresIn)
            tokenRepository.save(existingToken)
        } else {
            // 없으면 새로 저장
            val accessTokenExpiration = Instant.now().plusSeconds(expiresIn.toLong()) // 만료 시간 설정
            val googleToken = GoogleTokenEntity(
                user = user,
                accessToken = accessToken,
                refreshToken = refreshToken,
                accessTokenExpiration = accessTokenExpiration
            )
            tokenRepository.save(googleToken)
        }
    }

    override fun getTokenByUser(user: UserEntity): GoogleTokenEntity? {
        return tokenRepository.findByUser(user)
    }

    override fun refreshAccessToken(user: UserEntity): GoogleTokenEntity? {
        val googleToken = tokenRepository.findByUser(user)
            ?: throw IllegalStateException("No token found for user")

        println("google:" + googleOAuthProperties.clientSecret)

        // Access Token이 만료되었는지 확인
        if (googleToken.isAccessTokenExpired()) {
            if (googleToken.refreshToken == null) {
                // Refresh Token이 없거나 만료되었을 경우 403 Forbidden 반환
                throw HttpClientErrorException(HttpStatus.FORBIDDEN, "Refresh token is expired or not available")
            }

            // Refresh Token으로 Access Token 갱신 요청
            val url = "https://oauth2.googleapis.com/token"
            val body = mapOf(
                "client_id" to googleOAuthProperties.clientId,
                "client_secret" to googleOAuthProperties.clientSecret,
                "refresh_token" to googleToken.refreshToken,
                "grant_type" to "refresh_token"
            )

            val response = restTemplate.postForEntity(url, body, Map::class.java)
            val newAccessToken = response.body?.get("access_token") as? String
            val expiresIn = response.body?.get("expires_in") as? Int

            if (newAccessToken != null && expiresIn != null) {
                // Access Token과 만료시간 갱신
                googleToken.updateAccessToken(newAccessToken, expiresIn)
                tokenRepository.save(googleToken)
            } else {
                throw HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Failed to refresh access token")
            }
        }

        return googleToken
    }

    override fun registerGoogleCalendarId(calendarId: String, courseId: Long): Boolean {

        val userId = securityManager.getAuthenticatedUserName() ?: return false
        val user = UserEntity(userId) //

        // 사용자에 대한 기존 Google Calendar 엔티티가 있는지 확인
        if (googleCalendarRepository.findByCalendarId(calendarId) != null) {
            return false
        }

        // 새로운 GoogleCalendarEntity 생성 및 저장
        val entity = GoogleCalendarEntity(0, calendarId, user ,courseId)
        googleCalendarRepository.save(entity)
        return true
    }

    override fun getCalendarInfoByCourseId(courseId: Long): List<CalendarIdResponseDto> {
        val result = googleCalendarRepository.findByCourseId(courseId)
        return result.map { CalendarIdResponseDto.toDto(it) }.toList()
    }

    override fun findManagerEmailSameCourse(courseId: Long): List<ManagerEmailResponseDto> {
        return userRepository.findManagerEmailSameCourse(courseId).map { ManagerEmailResponseDto.toDto(it) } .toList()
    }

    private fun GoogleTokenEntity.isAccessTokenExpired(): Boolean {
        // Access Token의 만료 시간을 기준으로 만료 여부 판단
        return Instant.now().isAfter(this.accessTokenExpiration)
    }
}
