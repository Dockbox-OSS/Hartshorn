import java.text.SimpleDateFormat

plugins {
    base
    java
    kotlin("jvm") version "1.3.70" apply false

    id("com.palantir.git-version") version "0.12.3"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        jcenter()
    }
    dependencies {
        classpath("org.ajoberstar:grgit:1.7.0")
    }
}

group = "org.dockbox.darwin"

var revision = ""
var date = ""

ext {
    val git = org.ajoberstar.grgit.Grgit.open(file(".git"))
    val format = SimpleDateFormat("dd-MM-yyyy")
    date = format.format(git.head().date)
    revision = "-${git.head().abbreviatedId}"
}

allprojects {

    apply(plugin="java")
    apply(plugin="com.github.johnrengelman.shadow")

    version = "$revision-$date"

    repositories {
        jcenter()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("http://ci.athion.net/job/FastAsyncWorldEdit/ws/mvn/")
        }
        maven {
            url = uri("https://maven.enginehub.org/repo/")
        }
        maven {
            url = uri("https://plotsquared.com/mvn/")
        }
    }

    dependencies {
        compileOnly("com.sk89q.worldedit", "worldedit-core", "6.1")
        compileOnly("com.boydti", "fawe-api", "latest")
        compileOnly("com.plotsquared", "plotsquared-api", "3.1")

        implementation("com.j256.ormlite", "ormlite-core", "5.1")
        implementation("com.j256.ormlite", "ormlite-jdbc", "5.1")

        compileOnly("org.slf4j", "slf4j-api", "1.7.25")

        compileOnly("net.dv8tion:JDA:4.ALPHA.0_76") {
            exclude("club.minnced", "opus-java")
        }
    }

    tasks {
        build {
            dependsOn(shadowJar)
        }
    }
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
    }
    register<Javadoc>("aggregatedJavadocs") {
        group = "documentation"
        description = "Generates aggregated Javadocs for all projects"

        this.setDestinationDir(File("./docs"))
        this.title = "DarwinCore #${version}"

        delete("./docs")

        subprojects.forEach { project ->
            project.getTasksByName("javadoc", false).forEach {
                if (it is Javadoc) {
                    this.source += it.source
                    this.classpath += it.classpath
                    this.excludes += it.excludes
                    this.includes += it.includes
                }
            }
        }
    }
}

dependencies {
    subprojects.forEach {
        archives(it)
    }
}
