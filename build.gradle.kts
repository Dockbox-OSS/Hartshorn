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

import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension

buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.owasp:dependency-check-gradle:7.1.0.1")
    }
}

plugins {
    java
    `java-library`
    id("org.cadixdev.licenser") version "0.6.1"
    id("org.checkerframework") version "0.6.5"
}

apply {
    plugin("org.owasp.dependencycheck")
}

version = "22.4"
group = "org.dockbox.hartshorn"

java.toolchain {
    this.languageVersion.set(JavaLanguageVersion.of(17))
}

configure<DependencyCheckExtension> {
    // Strict rule, even small vulnerabilities should be handled unless they are suppressed
    failBuildOnCVSS = 1F
    failOnError = true
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("org.checkerframework")
        plugin("org.cadixdev.licenser")
    }

    license {
        header.set(resources.text.fromFile(rootProject.file("HEADER.txt")))
        ignoreFailures.set(false)
        include(
                "**/*.java",
                "**/*.kt",
                "**/*.groovy",
                "**/*.scala",
                "**/*.yml",
                "**/*.properties",
                "**/*.toml",
        )
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    repositories {
        mavenCentral()
    }

    group = rootProject.group
    version = rootProject.version

    description = "${project.name} ${project.version} (${this.name})"

    base.archivesName.set(this.name)

    configurations {
        testImplementation {
            extendsFrom(configurations.implementation.get())
        }
        all {
            exclude(group = "junit", module = "junit")
            resolutionStrategy.dependencySubstitution {
                rootDir.listFiles()?.forEach {
                    if (it.isDirectory && File(it, "${it.name}.gradle.kts").exists()) {
                        substitute(module("org.dockbox.hartshorn:${it.name}"))
                            .using(project(":${it.name}"))
                    }
                }
            }
        }
    }

    dependencies {
        // Can't use `libs` directly in the Kotlin DSL in the `allprojects` block, so we need to target
        // the root project manually. See https://github.com/gradle/gradle/issues/16634 for reference.
        // This is not required in child projects, only in this block.
        implementation(rootProject.libs.slf4j)
        implementation(rootProject.libs.bundles.jakarta)

        testImplementation(project(":hartshorn-test-suite"))
        testImplementation(rootProject.libs.bundles.test)
        testImplementation(rootProject.libs.bundles.testRuntime)
    }

    tasks {
        // Register custom tasks
        register<Copy>("copyArtifacts") {
            doLast {
                val version = project.version
                val destinationFolder = "$rootDir/hartshorn-assembly/distributions/$version"
                val sourceFolder = "$buildDir/libs"

                from(sourceFolder)
                include("*$version*.jar")
                into(destinationFolder)
            }
        }

        // Configure existing tasks
        test {
            useJUnitPlatform()
            val maxWorkerCount = gradle.startParameter.maxWorkerCount
            maxParallelForks = if (maxWorkerCount < 2) 1 else maxWorkerCount / 2
        }
        build {
            dependsOn(updateLicenses)
            finalizedBy(findByName("copyArtifacts"), clean)
        }

        // Target existing tasks by type
        withType<JavaCompile> {
            options.compilerArgs.add("-parameters")
            options.encoding = "UTF-8"
        }
        withType<Javadoc> {
            isFailOnError = false
            (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            (options as StandardJavadocDocletOptions).addStringOption("encoding", "UTF-8")
            (options as StandardJavadocDocletOptions).addStringOption("charSet", "UTF-8")
        }
    }
}
