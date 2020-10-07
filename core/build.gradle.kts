/**
 * CORE PROJECT PLUGINS
 */
plugins {
    // Spring Boot project build plugins
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.springframework.boot") version "2.3.3.RELEASE"
}

/**
 * CORE IMPLEMENTATION AND API
 */
dependencies {
    // Chosen JSON API and serialization engine
    api("com.google.code.gson", "gson", "2.8.6")

    // Spring Boot starter and embedded Tomcat
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-tomcat")

    // Piggy-back off Spring's logs
    api("org.slf4j", "slf4j-api", "1.7.30")

    // JSP convenience tab libraries
    implementation("javax.servlet.jsp.jstl", "jstl-api", "1.2")
}