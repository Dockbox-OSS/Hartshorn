<p align="center"><img alt="Hartshorn" src="./hartshorn-assembly/images/logo_shadow.png" height="150" /></p>
<h1 align="center">Hartshorn Framework</h1>
<p align="center"><img src="https://github.com/GuusLieben/Hartshorn/actions/workflows/hartshorn.yml/badge.svg"></p>
<p align="center"><img src="https://img.shields.io/badge/JDK%20source-17-white"> <img src="https://img.shields.io/badge/JDK%20target-19-white"></p>

[Hartshorn](https://hartshorn.dockbox.org/) is a modern JVM-based full stack Java framework. It is capable of aiding developers while building modular, testable, and scalable applications with support for Java and other JVM languages. Hartshorn aims to ease the creation and management of complex JVM applications, this is done by providing tools necessary to build these applications. You can read more about Hartshorn's core technologies and principles in the [core technologies](https://hartshorn.dockbox.org/core/cdi/) topic.

## Getting started

If you are just getting started with hartshorn, you'll want to view the [Getting Started](https://hartshorn.dockbox.org/getting-started/setup/) guides on the official documentation website. The provided guides use Hartshorn's application starter which enables you to get started quickly. If you are just looking for the Maven dependencies, these are listed below.

### Maven configuration

Each module has its own dedicated dependency, so you have the freedom to use only the modules you actually need. You can find all modules and their respective releases on [Maven Central](https://central.sonatype.dev/namespace/org.dockbox.hartshorn).

To get started, add the Maven dependency:

```xml
<dependency>
  <groupId>org.dockbox.hartshorn</groupId>
  <artifactId>hartshorn-core</artifactId>
  <version>${version}</version>
</dependency>
```

Or if you are using Gradle:

```groovy
implementation "org.dockbox.hartshorn:hartshorn-core:$version"
```

Depending on which module you're using, you may also need to import a specific implementation. For example, Hartshorn JPA comes with a Hibernate implementation, which you can import using `hartshorn-jpa-hibernate`. Implementations will always transitively import their API counterpart(s).

### Starting your first application

Getting started with Hartshorn is easy, the example below will introduce you to the basics of setting up and running your first application.

**TODO: Write content here**

## Building Hartshorn

If you wish to build Hartshorn yourself, either to get access to pre-release versions, or to add customizations, the guide below explains how to build usable JAR artifacts.  All platforms require a Java installation, with JDK 17 or more recent version.

Before building, ensure you set the JAVA\_HOME environment variable. For example:


| Platform | Command                                              |
| :------: | ---------------------------------------------------- |
|   Unix   | ``export JAVA_HOME=/usr/lib/jvm/openjdk-17-jdk``     |
|   OSX   | ``export JAVA_HOME=`/usr/libexec/java_home -v 17` `` |
| Windows | ``set JAVA_HOME="C:\Program Files\Java\jdk-17.0.3"`` |

Hartshorn uses Gradle to automate builds, performing several steps before and after a build has completed.
Depending on your IDE the Gradle wrapper may be automatically used. If you encounter any issues, use `./gradlew` for Unix systems or  `gradlew.bat` for Windows systems in place of any `gradle` command.

To build all Hartshorn modules at once, run `gradle build`. To build specific modules, run `gradle :hartshorn-$module:build`.

Once the build completes, the project distribution archives will be installed at `/hartshorn-assembly/distributions` in the base directory.
Builds are versioned by release versions, with the artifact following the format `hartshorn-$module-$version.jar`. This will also generate appropriate `javadoc` and `sources` artifacts.

## Contributing

Interested in contributing to Hartshorn, want to report a bug, or have a question? Before you do, please read the [contribution guidelines](https://hartshorn.dockbox.org/contributing/)
