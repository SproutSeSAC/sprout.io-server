package io.sprout.api.config

import io.sprout.api.auth.filter.JwtFilter
import io.sprout.api.auth.security.handler.CustomAuthenticationFailureHandler
import io.sprout.api.auth.security.handler.CustomAuthenticationSuccessHandler
import io.sprout.api.auth.security.manager.CustomAuthorizationRequestResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler,
    private val customAuthenticationFailureHandler: CustomAuthenticationFailureHandler,
    private val customAuthorizationRequestResolver: CustomAuthorizationRequestResolver,
) {

    private val whiteList = arrayOf("/api/**")

    @Bean
    fun filterChain(http: HttpSecurity, jwtFilter: JwtFilter): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .cors { }
            .formLogin { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(*whiteList).permitAll()
                it.anyRequest().permitAll()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .oauth2Login { config ->
                config.authorizationEndpoint {
                    it.authorizationRequestResolver(customAuthorizationRequestResolver)
                }
                config.successHandler(customAuthenticationSuccessHandler)
                config.failureHandler(customAuthenticationFailureHandler)
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
