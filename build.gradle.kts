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

import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import java.time.LocalDate
import java.time.format.DateTimeFormatter

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.owasp:dependency-check-gradle:7.4.1")
    }
}

plugins {
    id("java")
    id("java-library")
    id("java-test-fixtures")

    // Custom plugins, can be found in the gradle/plugins directory
    id("org.dockbox.hartshorn.gradle.javadoc")

    // Required for CI and to automatically update license headers on build
    id("org.cadixdev.licenser") version "0.6.1"
}

apply {
    plugin("org.owasp.dependencycheck")
}

version = "22.5"
group = "org.dockbox.hartshorn"

java {
    // Development is only performed using the latest LTS Java version.
    sourceCompatibility = JavaVersion.VERSION_17

    // Targeting the latest non-incubating Java version, to ensure compatibility
    // with the latest Java version.
    targetCompatibility = JavaVersion.VERSION_19
}

configure<DependencyCheckExtension> {
    // Strict rule, even small vulnerabilities should be handled unless they are suppressed
    failBuildOnCVSS = 1F
    failOnError = true
    suppressionFiles = listOf("gradle/dependency-check-suppressions.xml")
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("org.cadixdev.licenser")
        plugin("org.dockbox.hartshorn.gradle.javadoc")
        plugin("java-test-fixtures")
    }

    license {
        header.set(resources.text.fromFile(rootProject.file("HEADER.txt")))
        
        ignoreFailures.set(false)
        properties {
            set("year", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")))
        }

        // CI will verify the license headers, but not update them. To ensure
        // invalid/missing headers are clearly visible, we fail the build if
        // headers are invalid.
        ignoreFailures.set(false)
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
        // Ensure that all standard dependencies are included in the test
        // classpath, so we don't need to explicitly add them to the test
        // classpath.
        testImplementation.get().extendsFrom(configurations.implementation.get())
        testFixturesImplementation.get().extendsFrom(configurations.implementation.get())

        all {
            resolutionStrategy {
                // Commonly used libraries that may be included in multiple
                // projects, but should be kept in sync across all projects.
                setForcedModules(
                        rootProject.libs.slf4j,
                        rootProject.libs.checkerQual,
                )

                dependencySubstitution {
                    fun substitute(baseDir: File, prefix: String = "") {
                        baseDir.listFiles()?.forEach { projectDir ->
                            // Allows for local development of Hartshorn modules. Instead of having to publish
                            // the module to a local Maven repository, we simply substitute the dependency with
                            // the local module.
                            //
                            // In practice, this means that if you have a dependency on "org.dockbox.hartshorn:hartshorn-core"
                            // in your build.gradle.kts file, and you have a local module named "hartshorn-core" in the
                            // root directory of the project, the dependency will be substituted with project(":hartshorn-core").
                            if (projectDir.isDirectory && File(projectDir, "${projectDir.name}.gradle.kts").exists()) {
                                val module = module("${group}:${projectDir.name}")
                                val project = project("${prefix}:${projectDir.name}")
                                substitute(module).using(project)

                                substitute(projectDir, ":${projectDir.name}")
                            }
                        }
                    }
                    substitute(rootDir)
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

        // Only require qualifiers to be present, we don't use CF to actually run analysis on the code
        // and we don't want to force users to use CF.
        implementation(rootProject.libs.checkerQual)

        testImplementation(project(":hartshorn-test-suite"))
        testImplementation(rootProject.libs.bundles.test)
        testImplementation(rootProject.libs.junitJupiterEngine)

        testFixturesImplementation(project(":hartshorn-test-suite"))
        testFixturesImplementation(rootProject.libs.bundles.test)
        testFixturesImplementation(rootProject.libs.junitJupiterEngine)
    }

    tasks {
        // Instead of using the default `libs` output directories, meaning that the output of each
        // module is placed in a separate directory, we use a single output directory for all modules.
        //
        // This is done to ensure that the output of all modules is placed in the same directory, and
        // can easily be accessed for further local development.
        register<Copy>("copyArtifacts") {
            val version = rootProject.version
            val destinationFolder = "$rootDir/hartshorn-assembly/distributions/$version"
            val sourceFolder = "$buildDir/libs"

            if (!File(destinationFolder).exists()) mkdir(destinationFolder)

            from(file(sourceFolder))
            include("*$version*.jar")
            into(file(destinationFolder))
        }

        // Configure existing tasks
        test {
            useJUnitPlatform()

            // When possible, run tests in parallel. This automatically limits to use only half
            // of the available workers, so we can still run other tasks in parallel. If there are
            // less than two workers available, we don't run in parallel.
            val maxWorkerCount = gradle.startParameter.maxWorkerCount
            maxParallelForks = if (maxWorkerCount < 2) 1 else maxWorkerCount / 2
        }

        build {
            dependsOn(updateLicenses)
            finalizedBy(findByName("copyArtifacts"), clean)
        }

        withType<JavaCompile> {
            // Ensure parameter names are kept in the compiled bytecode. This is required for
            // some reflection actions to work optimally. While this is not required for
            // Hartshorn to function, it is recommended to keep this enabled.
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
