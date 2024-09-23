package io.sprout.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): GroupedOpenApi {
        val paths = arrayOf("/**")

        return GroupedOpenApi.builder()
            .group("Sprout-dev API")
            .pathsToMatch(*paths)
            .build()
    }

    @Bean
    fun customOpenApi(): OpenAPI {
        val components: Components = Components()
            .addSecuritySchemes("Access-Token", getAccessTokenSecurityScheme())
            .addSecuritySchemes("Refresh-Token", getRefreshTokenSecurityScheme())

        val securityTokens: SecurityRequirement = SecurityRequirement()
            .addList("Access-Token")
            .addList("Refresh-Token")

        return OpenAPI()
            .info(Info().title("Sprout-dev API").version("1.0").description("SPROUT API description"))
            .components(components)
            .addSecurityItem(securityTokens)
    }

    private fun getAccessTokenSecurityScheme(): SecurityScheme {
        return SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("Access-Token")

    }

    private fun getRefreshTokenSecurityScheme(): SecurityScheme {
        return SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("Refresh-Token")
    }
}