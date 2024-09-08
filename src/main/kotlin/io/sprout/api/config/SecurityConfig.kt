package io.sprout.api.config

import io.sprout.api.auth.security.handler.CustomAuthenticationFailureHandler
import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler,
    private val customAuthenticationFailureHandler: CustomAuthenticationFailureHandler
) {

    private val whiteList = arrayOf("/api/**")

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http.csrf { it.disable() }
        http.cors { it.disable() }
        http.formLogin { it.disable() }
        http.authorizeHttpRequests {
            it.requestMatchers(*whiteList).permitAll()
            it.anyRequest().permitAll()
        }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.oauth2Login {
            it.successHandler(customAuthenticationSuccessHandler)
            it.failureHandler(customAuthenticationFailureHandler)
        }
        return http.build()
    }
}