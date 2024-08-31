plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "io.sprout"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
	annotation("com.fasterxml.jackson.annotation.JsonInclude")
}

noArg {
	// @Entity가 붙은 클래스에 한해서만 no arg 플러그인을 적용
	annotation("jakarta.persistence.Entity")
	annotation("com.fasterxml.jackson.annotation.JsonInclude")
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// auth
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

	// db
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// jwt
	val jwtVersion = "0.11.5"
	implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
	implementation("io.jsonwebtoken:jjwt-impl:$jwtVersion")
	implementation("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

	// mvc
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

	//swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	exclude("**/*")
	useJUnitPlatform()
}

tasks.jar {
	enabled = false
}
