import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	id ("de.undercouch.download") version "3.4.3"
	kotlin("jvm") version "1.8.0"
	kotlin("plugin.spring") version "1.8.0"
	kotlin("plugin.jpa") version "1.8.0"
	jacoco
}

group = "com.renato"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val nimbusJose4jVersion = "9.31"
val springDocVersion = "2.0.2"
val postgreSQLVersion = "42.5.4"
val newRelicJava = "8.0.1"
val liquibaseVersion = "4.19.1"
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
	implementation("org.springframework.boot:spring-boot-starter-validation")
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
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
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