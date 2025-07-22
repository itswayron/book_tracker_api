import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
  id("org.springframework.boot") version "3.5.3"
  id("io.spring.dependency-management") version "1.1.5"
}

group = "dev.wayron"
version = "0.0.1-SNAPSHOT"
description = "A tracker for your books and readings!"

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("org.springframework.boot:spring-boot-devtools")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("com.github.f4b6a3:uuid-creator:4.3.1")
  implementation("io.jsonwebtoken:jjwt-api:0.12.6")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
  implementation("org.flywaydb:flyway-core:11.8.2")
  runtimeOnly("org.flywaydb:flyway-database-postgresql:11.8.2")
  implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
  implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.10.1")
  implementation("com.twelvemonkeys.imageio:imageio-core:3.12.0")
  implementation("com.twelvemonkeys.imageio:imageio-webp:3.12.0")
  implementation("org.postgresql:postgresql:42.7.5")
  implementation("org.springframework.boot:spring-boot-starter-mail:3.5.3")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.mockito:mockito-core:5.18.0")
  testImplementation("io.mockk:mockk:1.14.5")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.test {
  jvmArgs("-XX:+EnableDynamicAgentLoading")
}

sourceSets {
  main {
    kotlin.srcDirs("src/main/kotlin")
  }
  test {
    kotlin.srcDirs("src/test/kotlin")
  }
}
