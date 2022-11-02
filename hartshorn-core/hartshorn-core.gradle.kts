import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Copyright 2019-2022 the original author or authors.
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
    kotlin("jvm") version "1.7.10"
    scala
    groovy
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

dependencies {
    api(libs.bundles.jakarta)
    api(libs.javassist)
    api(libs.cglib)

    implementation(libs.logback)

    testImplementation(libs.scala)
    testImplementation(libs.groovy)
}
