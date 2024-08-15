package com.sesac.climb_mates.config.auth

import com.sesac.climb_mates.config.auth.oath2.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customSuccessHandler: CustomSuccessHandler,
    private val customFailureHandler: CustomFailureHandler,
    private val customOAuth2UserService: CustomOAuth2UserService
) {
    @Bean
    fun passwordEncoder():PasswordEncoder{
        return  BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration):AuthenticationManager{
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun filterChain(http:HttpSecurity): SecurityFilterChain{
        http.csrf { it.disable() }
        http.authorizeHttpRequests{
            it.requestMatchers("/css/**", "/script/**", "/img/**", "/error/**", "/sms/**",
                "/api/**", "/test/**", "/account/**").permitAll()
                .requestMatchers("/login/**").not().authenticated()
                .anyRequest().authenticated()
        }
        http.formLogin{
            it
                .loginPage("/login")
                .loginProcessingUrl("/login/action")
                .successHandler(customSuccessHandler)
                .failureHandler(customFailureHandler)
                .permitAll()
        }
        http.oauth2Login { configure ->
            configure.loginPage("/login")
            configure.userInfoEndpoint{
                it.userService(customOAuth2UserService)
            }
            configure.successHandler(customSuccessHandler)
            configure.failureHandler(customFailureHandler)
        }
        http.logout {
            it.deleteCookies("JSESSIONID")
            it.invalidateHttpSession(true)
            it.logoutUrl("/logout").permitAll()
            it.logoutSuccessUrl("/login")
        }
        return http.build()
    }
}