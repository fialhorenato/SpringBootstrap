import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id ("de.undercouch.download") version "3.4.3"
	kotlin("jvm") version "1.7.0"
	kotlin("plugin.spring") version "1.7.0"
	kotlin("plugin.jpa") version "1.7.0"
	jacoco
}

group = "com.renato"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val nimbusJose4jVersion = "9.23"
val springDocVersion = "1.6.9"
val postgreSQLVersion = "42.4.0"
val newRelicJava = "7.10.0"
val liquibaseVersion = "4.12.0"
val h2databaseVersion = "2.1.214"


tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadNewrelic") {
	mkdir("newrelic")
	src("https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip")
	dest(file("newrelic"))
}
tasks.register<Copy>("unzipNewrelic") {
	from(zipTree(file("newrelic/newrelic-java.zip")))
	into(rootDir)
}

repositories {
	mavenCentral()
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		csv.required.set(true)
		html.required.set(true)
		html.outputLocation.set(file("$buildDir/reports/coverage"))
	}
}

configurations {
	implementation.configure {
		exclude(module = "spring-boot-starter-tomcat")
		exclude("org.apache.tomcat")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude(module = "spring-boot-starter-tomcat")
		exclude("org.apache.tomcat")
	}
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework.boot:spring-boot-starter-undertow")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.liquibase:liquibase-core:$liquibaseVersion")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJose4jVersion")
	implementation ("org.springdoc:springdoc-openapi-ui:$springDocVersion")
	implementation ("org.postgresql:postgresql:$postgreSQLVersion")
	implementation("com.newrelic.agent.java:newrelic-java:$newRelicJava")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2:$h2databaseVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}



tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.bootJar {
	archiveBaseName.set("app")
	archiveVersion.set("")
}

tasks.withType<Test> {
	useJUnitPlatform()
}