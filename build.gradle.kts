import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

application {
    mainClassName = "adventofcode.day10.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.cronutils:cron-utils:8.0.0")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")
    implementation("com.marcinmoskala:DiscreteMathToolkit:1.0.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}