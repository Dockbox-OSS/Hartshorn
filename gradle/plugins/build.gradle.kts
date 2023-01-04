/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    properties {
        set("year", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")))
    }

    // CI will verify the license headers, but not update them. To ensure
    // invalid/missing headers are clearly visible, we fail the build if
    // headers are invalid.
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