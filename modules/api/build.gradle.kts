import org.jetbrains.kotlin.ir.backend.js.compile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.1.0"
}

group = "com.uogames"
version = "0.3.7"
application {
    mainClass.set("com.uogames.dictionary.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

ktor {
    fatJar {
        archiveFileName.set("fat-$version.jar")
    }
    docker{
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        localImageName.set("sample-docker-image")
        imageTag.set("0.0.1-preview")
    }
}

sourceSets{

}

dependencies {

    implementation(project(":modules:version3"))
    //implementation(npm(":modules:version3"))


    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

    // ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-tomcat-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-http-redirect:$ktor_version")

    // database
    implementation ("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation ("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation ("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

    // runtimeOnly ("mysql:mysql-connector-java:8.0.30")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation ("com.zaxxer:HikariCP:5.0.1")

    // logging
    implementation("ch.qos.logback:logback-classic:$logback_version")


}