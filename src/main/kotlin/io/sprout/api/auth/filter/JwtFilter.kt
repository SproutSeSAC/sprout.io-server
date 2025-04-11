package io.sprout.api.auth.filter

import com.nimbusds.common.contenttype.ContentType
import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.service.TokenValidatorService
import io.sprout.api.config.exception.ExceptionCode
import io.sprout.api.config.exception.ExceptionCode.*
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.weaver.tools.cache.SimpleCacheFactory.path
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val tokenValidatorService: TokenValidatorService,
    private val securityManager: SecurityManager
) : OncePerRequestFilter() {


    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludePath = arrayOf(
            "/api/swagger-ui/swagger-initializer.js",
            "/api/swagger-ui/index.css",
            "/api/swagger-ui/swagger-ui-bundle.js",
            "/api/swagger-ui/swagger-ui-standalone-preset.js",
            "/api/swagger-ui/favicon-32x32.png",
            "/api/v3/api-docs/swagger-config",
            "/api/v3/api-docs/Sprout-dev%20API",
            "/api/api-docs/Sprout-dev%20API",
            "/api/api-docs/swagger-config",
            "/api/swagger-ui/index.html",
            "/api/swagger-ui/swagger-ui.css",
            "/api/swagger-ui.html",
            "/api/swagger-ui/swagger-ui.css.map",
            "/api/swagger-ui/swagger-ui-bundle.js.map",
            "/api/swagger-ui/swagger-ui-standalone-preset.js.map",
            "/api/v3/api-docs", // swagger
            "/api/login/refresh",
            "/api/login",
            "/api/login/success",
            "/api/login/fail",
            "/api/login/test",
            "/h2-console",
            "/favicon.ico",
            "/api/oauth2/authorization/google",// Corrected path,
            "/api/test/**",

            // 테스트를 위한 임시 url
//            "/api/store/list",
//            "/api/store/**",
//            "/api/user/check",
            "/api/user/nickname/duplicate", // 닉네임 중복 확인 - 필수회원이 아니면 304 에러 발생

            "/api/store/filterCount",
            "/api/course/list/**",
            "/api/campus/list",
            "/api/specifications/**",
            "/api/user/register",
            "/api/user/verification/**",
            "/api/notifications/admin",
            "/api/aws/**",
        )
        val path = request.requestURI
        logger.info ( "Request Path: $path" )

        val pathMatcher = AntPathMatcher()

        // 경로가 정확히 일치하는 경우에만 필터링을 통과시킴
//        return excludePath.any { exclude -> path == exclude }

        // 테스트를 위한 임시 url 허용
        return excludePath.any { exclude -> pathMatcher.match(exclude, path) }

    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        extractTokensFromRequest(request)
        val accessJws = request.getAttribute("accessJws") as String?
        logger.info("accessJws: $accessJws")

        /**
         *  토큰 header 없을시 404
         */
        if (tokenValidatorService.isNotExistToken(accessJws, response)) {
            writeBody(NOT_EXIST_TOKEN, response)
            return
        }
        logger.debug { "accessToken: $accessJws" }

        /**
         ** 토큰 만료시 401 refresh 로 다시 요망
         */
        if (tokenValidatorService.isInvalidAccessToken(accessJws, response)) {
            writeBody(INVALID_TOKEN, response)
            return
        }

        /**
         *  필수정부 입력회원 아닐시 304호출
         *  필수정보 입력하는 경우에는 해당 필터 적용 안된도록 수정하겠음 ()
         */
        if (tokenValidatorService.isNotEssentialUserToken(accessJws!!, response)) {
            writeBody(NOT_ESSENTIAL_USER, response)
            return
        }

        securityManager.setUpSecurityContext(accessJws, request)
        filterChain.doFilter(request, response)
    }

    private fun getTokenFromCookies(cookies: Array<Cookie>?, tokenName: String): String? {
        return cookies?.firstOrNull { it.name == tokenName }?.value
    }

    private fun extractTokensFromRequest(request: HttpServletRequest) {
        var accessJws: String? = request.getHeader("Access-Token")

        if (accessJws == null) {
            val cookies = request.cookies
            accessJws = getTokenFromCookies(cookies, "access_token")
        }

        request.setAttribute("accessJws", accessJws)
    }

    private fun writeBody(exceptionCode: ExceptionCode, response: HttpServletResponse) {
        response.status = exceptionCode.httpStatus.value()
        response.contentType = ContentType.APPLICATION_JSON.toString()

        response.writer.write("{\"message\": \"${exceptionCode.message}\"}")
    }
}