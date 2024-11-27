plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.jetbrains.kotlin.kapt") version "1.9.24"
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
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("com.squareup.retrofit2:converter-gson:2.9.0")

	// db
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")


	//개발환경 변경 감지 재시작
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// jwt
	val jwtVersion = "0.11.5"
	implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
	implementation("io.jsonwebtoken:jjwt-impl:$jwtVersion")
	implementation("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

	// mvc
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

	// AWS
	implementation(platform("software.amazon.awssdk:bom:2.20.158"))
	implementation("software.amazon.awssdk:s3")
	implementation("software.amazon.awssdk:auth")

	// queryDsl
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")  // QueryDSL JPA 지원
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")  // Annotation Processor
	kapt("jakarta.persistence:jakarta.persistence-api")  // JPA API

	// test code 관련
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testRuntimeOnly("com.h2database:h2")

	//aws s3
//	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

	// webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")
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

tasks.withType<JavaCompile> {
	options.generatedSourceOutputDirectory.set(file("build/generated/sources/annotationProcessor/java/main"))
}

// spring boot 2.5.0 이후 버전일 경우 plain.jar 생성 방지
tasks.jar {
	enabled = false
}
