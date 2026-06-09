import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Standard Kotlin JVM plugin
    kotlin("jvm") version "2.4.0"
    // Application plugin to easily run the project from Gradle
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Standard Kotlin library dependencies
    implementation(kotlin("stdlib"))

    // If you use any external JSON or testing frameworks, add them here
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// Configure the application plugin to point to your new Java Main class
application {
    mainClass.set("ui.Main")
}

tasks.test {
    useJUnitPlatform()
}

// Tells Gradle to compile Java and Kotlin seamlessly together
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // FIX: Using JvmTarget.fromTarget("24") works dynamically
        // even if the constant JVM_24 is missing from your plugin version
        jvmTarget.set(JvmTarget.fromTarget("24"))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}