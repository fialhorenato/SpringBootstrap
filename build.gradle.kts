import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "2.2.21"
	val springBootVersion = "4.0.0"
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

kotlin {
    jvmToolchain(24)
}

val nimbusJose4jVersion = "10.6"
val springDocVersion = "2.8.14"
val postgreSQLVersion = "42.7.8"
val liquibaseVersion = "5.0.1"
val h2databaseVersion = "2.4.240"
val mockitoVersion = "5.20.0";
val mockitoKotlinVersion = "6.1.0"
val mockkVersion = "1.13.10"


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

    //H2
    implementation("org.springframework.boot:spring-boot-h2console")
    runtimeOnly("com.h2database:h2")

    //PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    //Prometheus
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJose4jVersion")
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
	testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("io.mockk:mockk-jvm:${mockkVersion}")

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
