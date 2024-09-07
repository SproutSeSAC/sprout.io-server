package io.sprout.api

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@SpringBootConfiguration
@EnableJpaRepositories
@ConfigurationPropertiesScan
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
