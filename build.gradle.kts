plugins {
    kotlin("jvm") version "2.0.20"
    application
    `maven-publish`
}

group = "dev.idot"
version = "0.3"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

publishing {
    publications.create<MavenPublication>("plugin") {
        artifact(
            tasks.register("mainJar", Jar::class) {
                archiveClassifier.set("")
                from(sourceSets["main"].output)
            }.get()
        )
        artifact(
            tasks.register("sourceJar", Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }.get()
        )
    }
}

tasks {
    test {
        filter.excludeTestsMatching("Benchmark")
    }
}