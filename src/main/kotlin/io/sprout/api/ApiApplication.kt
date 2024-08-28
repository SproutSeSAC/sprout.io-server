package io.sprout.api

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@SpringBootConfiguration
@ConfigurationPropertiesScan
class ApiApplication

fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
