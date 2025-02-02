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
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    //redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")


    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // test
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    // Http
    implementation ("org.springframework.boot:spring-boot-starter-web")
    // WebClient 의존성
    implementation ("org.springframework.boot:spring-boot-starter-webflux")
    implementation ("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64") // 최신 버전 확인
    implementation ("org.springframework.boot:spring-boot-starter-web")

    implementation("com.opencsv:opencsv:5.7.1")

    implementation ("org.springframework.boot:spring-boot-starter-cache")
    implementation ("com.github.ben-manes.caffeine:caffeine")

    //fileUtil
    implementation("commons-io:commons-io:2.13.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
        ignoreFailures = true  // 테스트 실패해도 빌드 진행
    }
    
    bootJar {
        archiveFileName.set("app.jar")
    }
    
    processResources {
        // 리소스 파일 복사 확인
        doFirst {
            println("Processing resources...")
        }
    }
}
