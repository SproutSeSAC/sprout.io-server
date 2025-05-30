package io.sprout.api.config

import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class QueryDslConfig(
    private val em: EntityManager
) {

    @Bean
    fun queryDsl(): JPAQueryFactory {
        return JPAQueryFactory(JPQLTemplates.DEFAULT,em)
    }

}