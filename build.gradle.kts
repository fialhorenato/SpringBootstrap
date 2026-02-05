import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "2.3.10"
	val springBootVersion = "4.0.2"
	val springDependencyVersion = "1.1.7"

	id("org.springframework.boot") version springBootVersion
	id("io.spring.dependency-management") version springDependencyVersion

	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion

	jacoco
}

group = "com.renato"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

val nimbusJose4jVersion = "10.7"
val springDocVersion = "3.0.1"


repositories {
	mavenCentral()
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		csv.required.set(true)
		html.required.set(true)
		html.outputLocation.set(File("${layout.buildDirectory.get().asFile}/reports/coverage"))
	}
}

dependencies {
	// Web
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

	// Actuator
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")

	// Data JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")

	// Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")

	// Liquibase
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")

	// H2
	implementation("org.springframework.boot:spring-boot-h2console")
	runtimeOnly("com.h2database:h2")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	// Prometheus
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	// Devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Nimbus Jose4j
	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJose4jVersion")

	// SpringDoc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.assertj", module = "assertj-core")
	}
	testImplementation("org.assertj:assertj-core:3.27.7")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
	}
}

tasks.bootJar {
	archiveBaseName.set("app")
	archiveVersion.set("")
}

tasks.test {
	useJUnitPlatform()
	jvmArgs = listOf("-Xshare:off", "-XX:+EnableDynamicAgentLoading")
}
