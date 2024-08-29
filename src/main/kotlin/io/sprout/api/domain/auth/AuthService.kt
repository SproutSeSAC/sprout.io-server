package io.sprout.api.domain.auth

import io.sprout.api.config.security.iwt.JwtDto
import io.sprout.api.domain.user.UserEntity
import io.sprout.api.domain.user.UserRepository
import io.sprout.api.infra.RetrofitService
import io.sprout.api.utils.CookieUtils
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.websocket.AuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val retrofitService: RetrofitService,
    private val httpServletResponse: HttpServletResponse,
    private val userRepository: UserRepository
) {
    companion object {
        const val REFRESH_TOKEN_COOKIE_NAME: String = "SPROUT"
    }

    @Transactional
    fun loginGoogle(request: AuthDto.AuthenticationGoogleRequest): JwtDto.Response {

        val userEntity = userRepository.findByEmail(request.email).orElseThrow {
            throw javax.naming.AuthenticationException("유저 아이디가 존재 하지 않습니다.")
        }

        val googleOauthInfo = retrofitService.inTokenIdOutGoogleUserInfo(request.tokenId)\
        if (googleOauthInfo != null) {
            val authentication = UsernamePasswordAuthenticationToken(
                userEntity.email,
                null,
                AuthorityUtils.createAuthorityList("ROLE_${userEntity.role}")
            )

            return loginSuccess(userEntity, authentication)
        }

        throw AuthenticationException("유저 아이디가 존재하지 않습니다.")
    }

    private fun loginSuccess(userEntity: UserEntity, authentication: UsernamePasswordAuthenticationToken): JwtDto.Response {

        val createJwtToken = jwtProvider.createJwtToken(userEntity, authentication)
        userEntity.lastLoginDateTime = LocalDateTime.now()

        CookieUtils.addCookie(
            response = httpServletResponse,
            name = REFRESH_TOKEN_COOKIE_NAME,
            value = createJwtToken.refreshToken,
            maxAge = createJwtToken.refreshTokenExpiresIn.toInt(),
        )

        try {
            userRepository.save(userEntity)
        } catch (e: Exception) {
            throw AuthenticationException("로그인에 실패하였습니다.")
        }

        return createJwtToken
    }


}