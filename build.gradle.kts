/**
 * GLOBAL PROJECT CONFIGURATION
 */
allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }

    apply {
        plugin("org.jetbrains.kotlin.jvm")

        plugin("java-library")
    }

    dependencies {
        // Kotlin platform and reflection libraries
        api(kotlin("stdlib"))
        api(kotlin("reflect"))

        // JUnit 5 (Jupiter) test platform
        testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
        // Embedded SQL database for testing JDBC operations
        testImplementation("org.hsqldb", "hsqldb", "2.5.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

/**
 * GLOBAL PROJECT PLUGINS
 */
plugins {
    kotlin("jvm") version "1.4.10" apply false

    `java-library`
}