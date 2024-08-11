package com.sesac.climb_mates.config.auth

import com.sesac.climb_mates.data.account.AccountRepository
import com.sesac.climb_mates.data.test.TestAccount
import com.sesac.climb_mates.data.test.TestAccountRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class CustomOAuth2UserService(
    private val accountRepository: TestAccountRepository
):OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        println("CustomOAuth2UserService")
        val oAuth2User: OAuth2User = DefaultOAuth2UserService().loadUser(userRequest)
        val attributes = oAuth2User.attributes

        // 사용자 정보 추출
        val userId = attributes["sub"] as String
        val email = attributes["email"] as String

        // 데이터베이스에서 사용자 조회
        val account = accountRepository.findByEmail(email).orElseGet {
            val newUser= TestAccount(sub=userId, email=email)
            accountRepository.save(newUser)
            println("${LocalDateTime.now()} - $newUser")
            newUser
        }

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority("USER")),
            attributes,
            "sub"
        )
    }
}