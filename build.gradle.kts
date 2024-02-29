repositories {
    mavenCentral()
    maven(url = "https://maven.google.com")

}


// apply false to declare version and skip version in subprojects
plugins {
    kotlin("jvm") version Kotlin.version
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springBootDependency
    id("org.jetbrains.kotlin.plugin.spring") version Kotlin.version
}


buildscript {
    repositories {
        mavenCentral()
        maven(url = "https://maven.google.com")

    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:${Kotlin.version}")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${Versions.springBoot}")
    }
}


repositories {
    mavenCentral()
    maven(url = "https://maven.google.com")

}

group = "me.underlow.tglog"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.github.pengrad:java-telegram-bot-api:7.0.1")
    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("com.github.docker-java:docker-java:3.3.5")
    implementation("com.github.docker-java:docker-java-transport-zerodep:3.3.5")

    implementation("com.cronutils:cron-utils:9.2.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        // exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${Versions.jupiter}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.jupiter}")

    testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")

    testImplementation("org.testcontainers:testcontainers:${Versions.testContainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testContainers}")
    // https://github.com/testcontainers/testcontainers-java/issues/3834
    // these lines required for Mac M1 at least for now
    testImplementation("net.java.dev.jna:jna-platform:5.8.0")
    testImplementation("net.java.dev.jna:jna:5.8.0")
    testImplementation("org.jeasy:easy-random-core:5.0.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}

springBoot {
    buildInfo()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString()
        allWarningsAsErrors = false
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
