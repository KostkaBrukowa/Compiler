import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.70"
    id("groovy")
    application
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "org.example.compiler.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("org.codehaus.groovy:groovy-all:2.5.2")
    testImplementation("org.hamcrest:hamcrest-core:1.2")
    testImplementation("cglib:cglib-nodep:3.3.0")
    testImplementation(kotlin("script-runtime"))

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=kotlin.contracts.ExperimentalContracts")
        jvmTarget = "1.8"
    }
}

