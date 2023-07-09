import io.ktor.plugin.features.*
import org.jetbrains.kotlin.fir.declarations.builder.buildTypeAlias
import org.jetbrains.kotlin.ir.backend.js.compile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("io.ktor.plugin") version "2.3.1"
}

group = "com.uogames"
version = "0.5.0"

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
        jreVersion.set(JreVersion.JRE_17)
        localImageName.set("sample-docker-image")
        imageTag.set("$version-preview")
        portMappings.set(listOf(
            DockerPortMapping(
                8081,
                8081,
                protocol = DockerPortMappingProtocol.TCP
            )
        ))
    }
}


tasks.register("version"){   
    println(version)
}

dependencies {

    implementation(project(":modules:version3"))
    implementation(project(":modules:test"))


    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")

    // ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-tomcat-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-http-redirect:$ktor_version")
    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")

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
