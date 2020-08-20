import java.time.LocalDate

plugins {
    java
    kotlin("jvm")
}

group = "org.dockbox.darwin.core"
val date = LocalDate.now().toString()

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.inject:guice:4.2.2")
    implementation("com.github.byteflux.libby:libby-core:-SNAPSHOT")
    testImplementation("junit:junit:4.12")

    // TODO : Convert to Libby
    implementation("org.reflections:reflections:0.9.11")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")
    implementation("org.apache.commons:commons-collections4:4.1")

    // TODO : Evaluate need
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.google.guava:guava:21.0")
}

tasks {
    "processResources"(ProcessResources::class) {
        from("src/main/resources") {
            include("darwin.properties")
            expand(
                    "version" to "$project.version",
                    "last_update" to date
            )
        }
    }
}
