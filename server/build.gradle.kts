plugins {
    kotlin("jvm")
}

val ktorVersion = "1.3.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":common"))

    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-html-builder", ktorVersion)

    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    implementation("org.postgresql", "postgresql", "42.1.4")

    implementation("com.beust", "klaxon", "5.2")
    implementation("org.json", "json", "20200518")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}