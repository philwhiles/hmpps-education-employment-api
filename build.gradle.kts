plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.4.1"
  kotlin("plugin.spring") version "1.7.10"
}

dependencyCheck {
  suppressionFiles.add("reactive-suppressions.xml")
  // Please remove the below suppressions once it has been suppressed in the DependencyCheck plugin (see this issue: https://github.com/jeremylong/DependencyCheck/issues/4616)
  suppressionFiles.add("postgres-suppressions.xml")
}

configurations {
  implementation { exclude(module = "spring-boot-starter-web") }
  implementation { exclude(module = "spring-boot-starter-tomcat") }
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  runtimeOnly("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("org.springframework.boot:spring-boot-starter-security")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.hibernate.validator:hibernate-validator")
  implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.9")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.6.9")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("io.jsonwebtoken:jjwt:0.9.1")
  implementation("io.opentelemetry:opentelemetry-api:1.16.0")
  implementation("io.r2dbc:r2dbc-postgresql:0.8.12.RELEASE")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  runtimeOnly("org.postgresql:r2dbc-postgresql:1.0.0.RC1")
  testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
  testImplementation("io.mockk:mockk:1.12.7")
  implementation("org.flywaydb:flyway-core:8.5.12")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3")
  testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3")

  runtimeOnly("org.postgresql:postgresql")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "17"
    }
  }
}
