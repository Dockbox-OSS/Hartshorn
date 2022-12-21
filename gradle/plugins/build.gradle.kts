plugins {
    id("java")
    id("java-gradle-plugin")
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "org.dockbox.hartshorn"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

license {
    header.set(resources.text.fromFile(rootProject.file("../../HEADER.txt")))
    ignoreFailures.set(false)
}

repositories {
    mavenCentral()
}

dependencies {
    // TODO: #907 Update once Gradle bumps to a higher version
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.17.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

gradlePlugin {
    plugins {
        create("javadocVerifierPlugin") {
            id = "org.dockbox.hartshorn.gradle.javadoc"
            implementationClass = "org.dockbox.hartshorn.gradle.javadoc.JavadocVerifierPlugin"
        }
    }
}