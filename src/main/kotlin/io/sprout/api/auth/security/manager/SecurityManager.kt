package io.sprout.api.auth.security.manager

import io.sprout.api.auth.token.domain.JwtToken
import jakarta.servlet.http.HttpServletRequest
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component

@Component
class SecurityManager(private val jwtToken: JwtToken){

     fun setUpSecurityContext(accessToken: String, request: HttpServletRequest) {
        val memberId = jwtToken.getUserIdFromAccessToken(accessToken)
        val userDetails: UserDetails = User(memberId, "", emptyList())
        val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    fun getAuthenticatedUserName(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication != null && authentication.principal is UserDetails) {
            (authentication.principal as UserDetails).username
        } else {
            null // 인증된 사용자가 없는 경우
        }
    }
}
