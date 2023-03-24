val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    // id("java")
    kotlin("jvm") version "1.8.0"
    `java-library`
}

group = "com.uogames.dictionary.v3"
version = "0.0.3"

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")

    // database
    implementation ("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation ("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation ("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}