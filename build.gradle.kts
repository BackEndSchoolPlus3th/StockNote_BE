plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.com"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //DB
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Security
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    testImplementation("org.springframework.security:spring-security-test")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")


    // JWT
//    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // test
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // WebClient 의존성
    implementation ("org.springframework.boot:spring-boot-starter-webflux")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
