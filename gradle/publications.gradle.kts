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
    plugin("maven-publish")
    plugin("signing")
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            pom {
                name.set(project.name as String)
                description.set(project.description as String)
                url.set("https://github.com/Dockbox-OSS/Hartshorn/")

                organization {
                    name.set("Hartshorn")
                    url.set("https://github.com/Dockbox-OSS")
                }
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://github.com/Dockbox-OSS/Hartshorn/blob/develop/LICENSE")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://www.github.com/Dockbox-OSS/Hartshorn")
                    connection.set("scm:git:git://github.com/Dockbox-OSS/Hartshorn")
                    developerConnection.set("scm:git:git://github.com/Dockbox-OSS/Hartshorn")
                }
                developers {
                    developer {
                        id.set("guuslieben")
                        name.set("Guus Lieben")
                        email.set("guuslieben@xendox.com")
                    }
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://www.github.com/Dockbox-OSS/Hartshorn/issues")
                }
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionResult()
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
    repositories {
        repositories {
            maven {
                name = "ossrh"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    if (project.properties["ossrh.username"] != null) username = project.properties["ossrh.username"] as String
                    if (project.properties["ossrh.password"] != null) password = project.properties["ossrh.password"] as String
                }
            }
        }
    }
}

configure<SigningExtension> {
    val publishingExtension = extensions.getByType(PublishingExtension::class)
    sign(publishingExtension.publications["mavenJava"])
}

tasks.register<Copy>("copyPublications") {
    doLast {
        val version = project.version

        val destinationFolder = "$rootDir/hartshorn-assembly/publications/$version"
        val libsFolder = "$buildDir/libs"
        val pomFolder = "$buildDir/publications/mavenJava"

        File(destinationFolder).mkdir()
        from(libsFolder)
        include("${project.name}-${version}*")
        into(destinationFolder)

        file("$pomFolder/pom-default.xml").renameTo(file("$pomFolder/${project.name}.pom"))
        file("$pomFolder/pom-default.xml.asc").renameTo(file("$pomFolder/${project.name}.pom.asc"))

        from(pomFolder)
        include("${project.name}.pom")
        into(destinationFolder)
    }
}

tasks.getByName("publishToMavenLocal") {
    dependsOn("generatePomFileForMavenJavaPublication")
    finalizedBy("copyPublications")
}
