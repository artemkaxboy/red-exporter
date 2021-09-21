// Created with:
// https://start.spring.io/#!type=gradle-project&language=kotlin&platformVersion=2.5.4&packaging=jar&jvmVersion=16&groupId=com.artemkaxboy&artifactId=redmine-exporter&name=redmine-exporter&description=Redmine%20metrics%20exporter%20for%20prometheus&packageName=com.artemkaxboy.redmineexporter&dependencies=mysql,data-jpa,prometheus,web

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
	kotlin("plugin.jpa") version "1.5.21"

	/*-------------------------------- JIB -----------------------------------------------*/
	id("com.google.cloud.tools.jib") version "3.0.0"
	id("org.ajoberstar.grgit") version "4.1.0"
	/*-------------------------------- JIB -----------------------------------------------*/
}

group = "com.artemkaxboy"
version = project.property("applicationVersion") as String
val minorVersion = "$version".replace("^(\\d+\\.\\d+).*$".toRegex(), "$1")
val majorVersion = "$version".replace("^(\\d+).*$".toRegex(), "$1")
java.sourceCompatibility = JavaVersion.VERSION_16

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

//	Cache
	implementation("org.springframework.boot:spring-boot-starter-cache")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "16"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

/*-------------------------------- JIB -----------------------------------------------*/
// https://stackoverflow.com/questions/55749856/gradle-dsl-method-not-found-versioncode
val lastCommit: Commit = Grgit.open { currentDir = projectDir }.head()
val lastCommitTime = "${lastCommit.dateTime}"
val lastCommitHash = lastCommit.id.take(8) // short commit id contains 8 chars

// System properties as systemProp.jib.to.auth.username cannot be set as env variable
// They suggest to use -Djib.to.auth.username instead:
// https://discuss.gradle.org/t/setting-properties-via-org-gradle-project--environment-variables-is-impossible-for-names-with-in-them/1896
// But github actions suggests to avoid passing secrets through the command-line
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
		tags = setOf("$version", minorVersion, majorVersion)
	}

	val author: String by project
	val sourceUrl: String by project

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
			"org.opencontainers.image.title" to name
		)
	}
}
/*-------------------------------- JIB -----------------------------------------------*/
