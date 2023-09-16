import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

apply {
    from("${project.rootDir}/gradle/publications.gradle.kts")
}

plugins {
    kotlin("jvm") version libs.versions.kotlin
    scala
    groovy
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
    }
}

dependencies {
    api("org.dockbox.hartshorn:hartshorn-introspect")

    testImplementation(libs.scala)
    testImplementation(libs.groovy)
}
