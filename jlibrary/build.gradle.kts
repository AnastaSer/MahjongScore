plugins {
    id("java")
}

base {
    archivesName.set("hand_scoring")
}

group = "org.example"
version = "1.0"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.mahjong.hand_scoring.ScoringCalculator"
    }

    // Берём все зависимости и упаковываем их внутрь JAR
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.apache.commons:commons-lang3:3.14.0")

    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}