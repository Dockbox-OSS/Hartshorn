plugins {
    java
    id("java-gradle-plugin")
}

group = "org.dockbox.hartshorn"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // TODO: #907 Update once Gradle bumps to a higher version
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.17.0")
}

gradlePlugin {
    plugins {
        create("javadocVerifierPlugin") {
            id = "org.dockbox.hartshorn.gradle.javadoc"
            implementationClass = "org.dockbox.hartshorn.gradle.javadoc.JavadocVerifierPlugin"
        }
    }
}