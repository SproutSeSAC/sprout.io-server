package io.sprout.api.user.service


import io.sprout.api.user.model.entities.GoogleTokenEntity
import io.sprout.api.user.model.entities.UserEntity
import io.sprout.api.user.repository.GoogleTokenRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.Instant

@Service
class GoogleTokenService(
    private val tokenRepository: GoogleTokenRepository,
    private val restTemplate: RestTemplate
) : GoogleUserService {
    override fun saveOrUpdateToken(user: UserEntity, accessToken: String, refreshToken: String?, expiresIn: Int) {
        val existingToken = tokenRepository.findByUser(user)
        if (existingToken != null) {
            // 기존 토큰이 있으면 Access Token과 만료 시간 갱신
            existingToken.updateAccessTokenAndRefreshToken(accessToken, refreshToken, expiresIn)
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

        // Access Token이 만료되었는지 확인
        if (googleToken.isAccessTokenExpired()) {
            if (googleToken.refreshToken == null) {
                // Refresh Token이 없거나 만료되었을 경우 403 Forbidden 반환
                throw HttpClientErrorException(HttpStatus.FORBIDDEN, "Refresh token is expired or not available")
            }

            // Refresh Token으로 Access Token 갱신 요청
            val url = "https://oauth2.googleapis.com/token"
            val body = mapOf(
                "client_id" to "YOUR_CLIENT_ID",
                "client_secret" to "YOUR_CLIENT_SECRET",
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

    private fun GoogleTokenEntity.isAccessTokenExpired(): Boolean {
        // Access Token의 만료 시간을 기준으로 만료 여부 판단
        return Instant.now().isAfter(this.accessTokenExpiration)
    }
}
