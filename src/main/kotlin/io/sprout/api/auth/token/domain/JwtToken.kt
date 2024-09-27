package io.sprout.api.auth.token.domain

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.sprout.api.config.properties.JwtPropertiesConfig
import io.sprout.api.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtToken(
    private val jwtPropertiesConfig: JwtPropertiesConfig,
    private val userRepository: UserRepository
) {
    // 비밀 키 생성 (여기선 256비트 HMAC 키를 생성)
    private val accessSecretKey =
        Keys.hmacShaKeyFor(jwtPropertiesConfig.accessToken.secret.toByteArray(StandardCharsets.UTF_8))


    // Access 토큰 생성
    fun createAccessTokenFromMemberId(memberId: Long, isValidMember: Boolean): String {

        val currentMs = System.currentTimeMillis()

        return Jwts.builder()
            .setSubject(memberId.toString())  // memberId를 subject로 설정
            .claim("isEssential", isValidMember)  // isEssential 클레임 추가
            .setExpiration(Date(currentMs + 1000 * jwtPropertiesConfig.accessToken.expiration))  // 만료 시간 설정
            .signWith(accessSecretKey)  // 비밀 키로 서명
            .setIssuedAt(Date(currentMs))  // 토큰 발행 시간
            .compact()  // 최종적으로 JWT 토큰을 생성
    }

    // Refresh 토큰 생성
    fun createRefreshToken(memberId: Long): String {
        val secretKey: SecretKey =
            Keys.hmacShaKeyFor(jwtPropertiesConfig.refreshToken.secret.toByteArray(StandardCharsets.UTF_8))
        val currentMs = System.currentTimeMillis()

        return Jwts.builder()
            .setSubject(memberId.toString())
            .setExpiration(Date(currentMs + 1000 * jwtPropertiesConfig.refreshToken.expiration))
            .signWith(secretKey)
            .setIssuedAt(Date(currentMs))
            .compact()
    }

    fun createAccessFromRefreshToken(refreshJws: String): String {

        val currentMs = System.currentTimeMillis()
        val user = userRepository.findByRefreshToken(refreshJws)
        return Jwts.builder()
            .setSubject(user!!.id.toString())  // memberId를 subject로 설정
            .claim("isEssential", user.isEssential)  // universityEmail 클레임 추가
            .setExpiration(Date(currentMs + 1000 * jwtPropertiesConfig.accessToken.expiration))  // 만료 시간 설정
            .signWith(accessSecretKey)  // 비밀 키로 서명
            .setIssuedAt(Date(currentMs))  // 토큰 발행 시간
            .compact()  // 최종적으로 JWT 토큰을 생성
    }

    // 토큰에서 사용자 Id 가져오기
    fun getUserIdFromAccessToken(token: String): String {

        return Jwts.parserBuilder()
            .setSigningKey(accessSecretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    }

    fun getIsEssentialEnsFromAccessToken(token: String): Boolean {

        val claims = Jwts.parserBuilder()
            .setSigningKey(accessSecretKey)
            .build()
            .parseClaimsJws(token)
            .body

        // 명시적으로 Boolean 값 변환
        val isEssential = claims["isEssential"]
        return when (isEssential) {
            is Boolean -> isEssential
            is String -> isEssential.toBoolean() // 클레임이 문자열로 저장된 경우
            else -> throw IllegalArgumentException("Invalid claim type for 'isEssential'")
        }
    }


    // 토큰의 유효성 검사
    fun isExpiredAccessToken(token: String): Boolean {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token) // 이 부분에서 만료된 토큰이면 ExpiredJwtException 발생
            val expiration = claims.body.expiration

            return expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            // 토큰이 만료되었을 경우 이 예외를 캐치
            return true // 만료되었음을 반환
        } catch (e: Exception) {
            // 그 외의 예외 처리
            return false
        }
    }
}