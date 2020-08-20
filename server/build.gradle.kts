plugins {
    kotlin("jvm")

    java
}

dependencies {
    api(project(":common", "default"))

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))


    api("com.beust", "klaxon", "5.4")

    implementation("org.postgresql", "postgresql", "42.2.15")
    implementation("javassist", "javassist", "3.12.1.GA")

    testImplementation("junit", "junit", "4.13")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}