import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "2.2.10"
	val springBootVersion = "3.5.5"
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
val javaJvmTarget = JvmTarget.JVM_21
val javaVersion = JavaVersion.VERSION_21

java.sourceCompatibility = javaVersion
val nimbusJose4jVersion = "10.4.2"
val springDocVersion = "2.8.10"
val postgreSQLVersion = "42.7.7"
val liquibaseVersion = "4.33.0"
val h2databaseVersion = "2.3.232"
val mockitoVersion = "5.19.0";
val mockitoKotlinVersion = "6.0.0"


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
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot", "spring-boot-starter-tomcat")
	}
	implementation("org.springframework.boot:spring-boot-starter-undertow")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("io.micrometer:micrometer-registry-prometheus")	
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.liquibase:liquibase-core:$liquibaseVersion")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJose4jVersion")
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
	implementation ("org.postgresql:postgresql:$postgreSQLVersion")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2:$h2databaseVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
	testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(javaJvmTarget)
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
