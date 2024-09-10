package io.sprout.api.auth.filter

import io.sprout.api.auth.security.manager.SecurityManager
import io.sprout.api.auth.token.service.TokenValidatorService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val tokenValidatorService: TokenValidatorService,
    private val securityManager: SecurityManager
) : OncePerRequestFilter() {


    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludePath = arrayOf(
            "/swagger-ui/swagger-initializer.js",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-ui.css",
            "/v3/api-docs", // swagger
            "/api/refresh",
            "/login",
            "/api/login",
            "/api/login/success",
            "/api/login/fail",
            "/h2-console",
            "/favicon.ico" // Corrected path
        )
        val path = request.requestURI
        logger.info { "Request Path: $path" }

        return excludePath.any { path.startsWith(it.trim()) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        extractTokensFromRequest(request)
        val accessJws = request.getAttribute("accessJws") as String?
        val refreshJws = request.getAttribute("refreshJws") as String?
        logger.info { "accessJws: $accessJws" }
        logger.info { "refreshJws: $refreshJws" }

        /**
         *  토큰 header 없을시 404
         */
        if (tokenValidatorService.isNotExistToken(accessJws, refreshJws, response)) return

        logger.debug { "accessToken: $accessJws" }
        /**
         ** 토큰 만료시 401 refresh 로 다시 요망
         */
        if (tokenValidatorService.isInvalidAccessToken(accessJws, response)) return
        /**
         *  필수정부 입력회원 아닐시 304호출
         */
        if (tokenValidatorService.isNotEssentialUserToken(accessJws!!, response)) return
        securityManager.setUpSecurityContext(accessJws, request)
        filterChain.doFilter(request, response)
    }

    private fun getTokenFromCookies(cookies: Array<Cookie>?, tokenName: String): String? {
        return cookies?.firstOrNull { it.name == tokenName }?.value
    }

    private fun extractTokensFromRequest(request: HttpServletRequest) {
        var accessJws: String? = request.getHeader("access-token")
        var refreshJws: String? = request.getHeader("refresh-token")

        if (accessJws == null && refreshJws == null) {
            val cookies = request.cookies
            accessJws = getTokenFromCookies(cookies, "access-token")
            refreshJws = getTokenFromCookies(cookies, "refresh-token")
        }

        request.setAttribute("accessJws", accessJws)
        request.setAttribute("refreshJws", refreshJws)
    }
}