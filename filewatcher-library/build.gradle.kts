plugins {
    kotlin("jvm");
    id("java-library")

}

group = "me.dmitry.filewatcher"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.methvin:directory-watcher:0.15.0") //https://github.com/gmethvin/directory-watcher
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}