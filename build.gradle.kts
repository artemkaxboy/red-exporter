// Created with:
// https://start.spring.io/#!type=gradle-project&language=kotlin&platformVersion=2.5.4&packaging=jar&jvmVersion=16&groupId=com.artemkaxboy&artifactId=redmine-exporter&name=redmine-exporter&description=Redmine%20metrics%20exporter%20for%20prometheus&packageName=com.artemkaxboy.redmineexporter&dependencies=mysql,data-jpa,prometheus,web

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	kotlin("plugin.jpa") version "1.4.32"

	/*-------------------------------- JIB -----------------------------------------------*/
	id("com.google.cloud.tools.jib") version "3.0.0"
	id("org.ajoberstar.grgit") version "4.1.0"
	/*-------------------------------- JIB -----------------------------------------------*/

	// kapt does not work with kotlin 1.5.21 ------ 22-Sep-2021
	kotlin("kapt") version "1.4.32"
}

group = "com.artemkaxboy"
version = project.property("applicationVersion") as String
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

// 	REST
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot","spring-boot-starter-tomcat")
	}
	implementation("org.springframework.boot:spring-boot-starter-jetty")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

//	Metrics
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

//	DB
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("mysql:mysql-connector-java")

//	Logging
	implementation("io.github.microutils:kotlin-logging:1.12.5")

//	Annotation processing
	compileOnly("org.springframework.boot:spring-boot-configuration-processor")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

//  https://codeburst.io/criteria-queries-and-jpa-metamodel-with-spring-boot-and-kotlin-9c82be54d626
//	Metamodels
	implementation ("org.hibernate:hibernate-jpamodelgen:5.5.7.Final")
	kapt("org.hibernate:hibernate-jpamodelgen:5.5.7.Final")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "15"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

/*-------------------------------- JIB -----------------------------------------------*/
// System properties as systemProp.jib.to.auth.username cannot be set as env variable
// They suggest to use -Djib.to.auth.username instead:
// https://discuss.gradle.org/t/setting-properties-via-org-gradle-project--environment-variables-is-impossible-for-names-with-in-them/1896
// But GitHub actions suggests avoiding passing secrets through the command-line
// https://docs.github.com/en/actions/reference/encrypted-secrets#using-encrypted-secrets-in-a-workflow
// That's why custom env variables are used here. At the same time we can use -Djib... command line options to override
// current envs.
val jibUsername = System.getenv("CONTAINER_REGISTRY_USERNAME") ?: ""
val jibPassword = System.getenv("CONTAINER_REGISTRY_TOKEN") ?: ""

jib {
	to {
		auth {
			username = jibUsername
			password = jibPassword
		}
	}

	val applicationDescription: String by project
	val lastCommitTime: String by project
	val lastCommitHash: String by project
	val author: String by project
	val sourceUrl: String by project
	val refName: String by project

    container {
		user = "999:999"
		creationTime = lastCommitTime
		ports = listOf("8080")

		environment = mapOf(
			"APPLICATION_NAME" to name,
			"APPLICATION_VERSION" to "$version",
			"APPLICATION_REVISION" to lastCommitHash
		)

		labels = mapOf(
			"maintainer" to author,
			"org.opencontainers.image.created" to lastCommitTime,
			"org.opencontainers.image.authors" to author,
			"org.opencontainers.image.url" to sourceUrl,
			"org.opencontainers.image.documentation" to sourceUrl,
			"org.opencontainers.image.source" to sourceUrl,
			"org.opencontainers.image.version" to "$version",
			"org.opencontainers.image.revision" to lastCommitHash,
			"org.opencontainers.image.vendor" to author,
			"org.opencontainers.image.ref.name" to refName,
			"org.opencontainers.image.title" to name,
			"org.opencontainers.image.description" to applicationDescription,
		)
	}
}
