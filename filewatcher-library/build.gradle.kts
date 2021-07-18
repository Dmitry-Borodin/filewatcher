plugins {
    kotlin("jvm");
    id("java-library")

}

group = "me.dmitry"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.methvin:directory-watcher:0.15.0") //https://github.com/gmethvin/directory-watcher
}

