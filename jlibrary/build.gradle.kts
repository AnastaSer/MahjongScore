plugins {
    id("java")
}

base {
    archivesName.set("hand_scorring")
}

group = "org.example"
version = "1.0"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.mahjong.hand_scoring.ScoringCalculator"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.14.0")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}