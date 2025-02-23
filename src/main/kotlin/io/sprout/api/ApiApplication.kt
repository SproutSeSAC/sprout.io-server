package io.sprout.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaRepositories
@EnableScheduling
class ApiApplication

fun main(args: Array<String>) {
	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	runApplication<ApiApplication>(*args)

	println("TimeZone: " + TimeZone.getDefault().id)
	println("LocalDateTime: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
}

