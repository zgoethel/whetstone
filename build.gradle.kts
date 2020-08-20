allprojects {
    repositories {
        jcenter()
        mavenCentral()

        maven("https://dl.bintray.com/kotlin/ktor")
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
}
