# Hartshorn Launchpad
Launchpad is a pre-configured application starter for Hartshorn. It provides a simple way to bootstrap your application with Maven, and get started with Hartshorn.

## Getting started
To get started, all you need to do is use the `hartshorn-launchpad` parent POM in your Maven project. This will automatically configure your project to use Hartshorn, and provide you with a simple application starter.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.dockbox.hartshorn</groupId>
        <artifactId>hartshorn-launchpad</artifactId>
        <version>0.5.0</version>
        <relativePath/> <!-- Lookup parent from repository -->
    </parent>

    <name>Your project name</name>
    <artifactId>your-project</artifactId>
</project>
```

## Using implementation-specific modules
Hartshorn provides a set of modules that are not included by default. These modules are implementation-specific, and are not required for the core functionality of Hartshorn. To use these modules, you need to add the corresponding dependency to your project. The following table lists the included modules, which do not need to be added manually.

| Module                 | Description                                                        | Default implementation            |
|------------------------|--------------------------------------------------------------------|-----------------------------------|
| `hartshorn-introspect` | Provides the ability to introspect classes and their members.      | `hartshorn-introspect-reflection` |
| `hartshorn-proxy`      | Provides the ability to create proxies for classes and interfaces. | `hartshorn-proxy-javassist`       |

## Using Launchpad with Gradle
Launchpad is not currently available for Gradle. However, you can still use Hartshorn with Gradle by using the [Spring Dependency Management plugin](https://spring.io/blog/2015/02/23/better-dependency-management-for-gradle).

```groovy
buildscript {
    dependencies {
        classpath "io.spring.gradle:dependency-management-plugin:$version"
    }
}

apply plugin: "io.spring.dependency-management"

dependencyManagement {
    imports {
        mavenBom "org.dockbox.hartshorn:hartshorn-bom:$version"
    }
}

dependencies {
    implementation "org.dockbox.hartshorn:hartshorn-core:$version"
    implementation "org.dockbox.hartshorn:hartshorn-introspect:$version"
    implementation "org.dockbox.hartshorn:hartshorn-proxy:$version"
}
```