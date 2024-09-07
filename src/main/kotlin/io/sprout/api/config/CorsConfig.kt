import io.sprout.api.config.properties.CorsPropertiesConfig
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.stereotype.Component

@Component
class CorsConfig(private val corsConfig: CorsPropertiesConfig) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://*:3000")  // 3000 포트로 오는 모든 IP 요청 허용
            .allowedOrigins(*corsConfig.allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
