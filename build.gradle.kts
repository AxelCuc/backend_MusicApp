plugins {
    kotlin("jvm") version "1.9.22"
    application
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.musica"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // PostgreSQL
    implementation("org.postgresql:postgresql:42.7.1")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}

application {
    mainClass.set("com.musica.MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}